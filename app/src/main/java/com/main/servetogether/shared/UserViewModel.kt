package com.main.servetogether.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState{
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val role: String) : AuthState()
}

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance("servedb")

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _userRole = MutableStateFlow<String?>(null)
    private val _userName = MutableStateFlow<String?>(null)
    private val _userSchool = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole
    val userName: StateFlow<String?> = _userName
    val userSchool: StateFlow<String?> = _userSchool

    init{
        fetchUserRole()
        fetchUser()
        fetchAffil()
    }

    fun fetchUserRole() {
        val currentUser = auth.currentUser

        if (currentUser != null){
            // fetch document upon log in
            viewModelScope.launch {
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            _userRole.value = document.getString("role") ?: "user"
                            val role = document.getString("role") ?: "user"
                            _authState.value = AuthState.Authenticated(role)
                        } else {
                            _userRole.value = "volunteer"
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Auth", "Failed to get role", e)
                        _userRole.value = "volunteer"
                        _authState.value = AuthState.Authenticated("volunteer")
                    }
            }
        } else {
            _userRole.value = ""
        }
    }

    fun fetchUser() {
        val currentUser = auth.currentUser

        if (currentUser != null){
            // fetch document upon log in
            viewModelScope.launch {
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            _userName.value = document.getString("username") ?: "user"
                        } else {
                            _userName.value = "unknown"
                        }
                    }
                    .addOnFailureListener {
                        _userName.value = "-failed to get username-"
                    }
            }
        } else {
            _userName.value = ""
        }
    }
    fun fetchAffil() {
        val currentUser = auth.currentUser

        if (currentUser != null){
            // fetch document upon log in
            viewModelScope.launch {
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            _userSchool.value = document.getString("school") ?: "user"
                        } else {
                            _userSchool.value = "unknown"
                        }
                    }
                    .addOnFailureListener {
                        _userSchool.value = "-failed to get school-"
                    }
            }
        } else {
            _userSchool.value = ""
        }
    }
}