package com.main.servetogether.shared

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    object Success : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance("servedb") // Use same instance as LoginScreen

    // Profile State
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    // Auth State
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // User Info
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userSchool = MutableStateFlow("")
    val userSchool: StateFlow<String> = _userSchool

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    // Profile Fields
    var fullName by mutableStateOf("")
        private set

    var gender by mutableStateOf("")
        private set

    var dateOfBirth by mutableStateOf("")
        private set

    var school by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var studentId by mutableStateOf("")
        private set

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated
            loadUserData()
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val document = db.collection("users").document(userId).get().await()

                if (document.exists()) {
                    // Load basic user info
                    _userName.value = document.getString("fullName") ?: ""
                    _userSchool.value = document.getString("school") ?: ""
                    _userRole.value = document.getString("role") ?: "volunteer"
                }
            } catch (e: Exception) {
                // Handle error silently or log it
            }
        }
    }

    fun loadProfileData() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            _profileState.value = ProfileState.Error("User not logged in")
            return
        }

        _profileState.value = ProfileState.Loading

        viewModelScope.launch {
            try {
                val document = db.collection("users").document(userId).get().await()

                if (document.exists()) {
                    fullName = document.getString("fullName") ?: ""
                    gender = document.getString("gender") ?: ""
                    school = document.getString("school") ?: ""
                    email = document.getString("email") ?: ""
                    studentId = document.getString("studentId") ?: ""

                    // Handle birthdate conversion
                    val birthdateMillis = document.getLong("birthdate")
                    dateOfBirth = if (birthdateMillis != null) {
                        dateFormatter.format(Date(birthdateMillis))
                    } else {
                        ""
                    }

                    _profileState.value = ProfileState.Success
                } else {
                    _profileState.value = ProfileState.Error("User data not found")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun updateProfile(
        newFullName: String,
        newGender: String,
        newDateOfBirth: String,
        newSchool: String,
        newEmail: String,
        newStudentId: String
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            _profileState.value = ProfileState.Error("User not logged in")
            return
        }

        _profileState.value = ProfileState.Loading

        viewModelScope.launch {
            try {
                // Convert date string back to millis if needed
                var birthdateMillis: Long? = null
                if (newDateOfBirth.isNotEmpty()) {
                    try {
                        birthdateMillis = dateFormatter.parse(newDateOfBirth)?.time
                    } catch (e: Exception) {
                        // Keep as null if parsing fails
                    }
                }

                val updates = hashMapOf<String, Any?>(
                    "fullName" to newFullName,
                    "gender" to newGender,
                    "birthdate" to birthdateMillis,
                    "school" to newSchool,
                    "email" to newEmail,
                    "studentId" to newStudentId
                )

                db.collection("users").document(userId).update(updates).await()

                // Update local state
                fullName = newFullName
                gender = newGender
                dateOfBirth = newDateOfBirth
                school = newSchool
                email = newEmail
                studentId = newStudentId

                // Update observable states
                _userName.value = newFullName
                _userSchool.value = newSchool

                _profileState.value = ProfileState.Success
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun resetProfileState() {
        _profileState.value = ProfileState.Idle
    }
}