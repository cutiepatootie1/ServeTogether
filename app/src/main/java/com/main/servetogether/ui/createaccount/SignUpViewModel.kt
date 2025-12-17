package com.main.servetogether.ui.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.servetogether.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Simple state to track what is happening
sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

class SignUpViewModel : ViewModel() {

    private val repository = AuthRepository()

    // The UI observes this variable
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(
        email: String,
        pass: String,
        fullName: String,
        school: String,
        birthdate: Long?,
        gender: String,
        role: String,
        studentId: String
    ) {
        _signUpState.value = SignUpState.Loading

        viewModelScope.launch {
            val result = repository.createAccount(
                email, pass, fullName, gender, school, birthdate, role,
                studentId = studentId
            )

            if (result.isSuccess) {
                _signUpState.value = SignUpState.Success
            } else {
                _signUpState.value = SignUpState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    // Reset state after showing error/success
    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}