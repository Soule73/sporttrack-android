package com.stapp.sporttrack.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stapp.sporttrack.data.models.AuthResponse
import com.stapp.sporttrack.data.models.LoginRequest
import com.stapp.sporttrack.data.models.UserRequest
import com.stapp.sporttrack.data.models.UserResponse
import com.stapp.sporttrack.data.repository.AuthRepository
import com.stapp.sporttrack.data.repository.CustomException
import com.stapp.sporttrack.ui.LoginActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class RegistrationViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var gender: String = ""
    var height: Float = 0f
    var weight: Float = 0f
    var password: String = ""

    private val _registrationResult = MutableStateFlow<Result<AuthResponse>?>(null)
    val registrationResult: StateFlow<Result<AuthResponse>?> = _registrationResult

    private val _loginResult = MutableStateFlow<Result<AuthResponse>?>(null)
    val loginResult: StateFlow<Result<AuthResponse>?> = _loginResult

    private val _verifyTokenResult = MutableStateFlow<Result<UserResponse>?>(null)
    val verifyTokenResult: StateFlow<Result<UserResponse>?> = _verifyTokenResult

    private val _registrationError = MutableStateFlow<Map<String, String>?>(null)
    val registrationError: StateFlow<Map<String, String>?> = _registrationError

    fun register() {
        viewModelScope.launch {
            val userRequest = UserRequest(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                birthDate = null, // Optional
                gender = gender,
                weight = if (weight.toDouble() > 0) weight.toDouble() else null,
                height = if (height.toDouble() > 0) height.toDouble() else null,
            )
            println("UserRequest: $userRequest")

            val result = authRepository.register(userRequest)
            if (result.isSuccess) {
                _registrationResult.value = result
                _registrationError.value = null
            } else {
                val error = result.exceptionOrNull()
                if (error is CustomException) {
                    _registrationError.value = parseErrorMessages(error)
                } else {
                    _registrationError.value = mapOf("error" to "Erreur inconnue")
                }
//                println("Erreur lors de l'inscription ---- ${registrationError.value?.get("email")}")
            }

        }

    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val loginRequest = LoginRequest(email, password)
            val result = authRepository.login(loginRequest)
            _loginResult.value = result
        }
    }

    fun logout(context: Context) {

        val intent = Intent(context, LoginActivity::class.java).apply {
            putExtra("checkAuthentication", false)
        }
        context.startActivity(intent)
        (context as Activity).finish()
        authRepository.logout()
    }

    fun verifyToken() {
        viewModelScope.launch {
            val result = authRepository.verifyToken()
            _verifyTokenResult.value = result
        }
    }

    private fun parseErrorMessages(exception: CustomException): Map<String, String> {
        return exception.errorResponse.errors
    }


    fun clearRegistrationError() {
        _registrationError.value = null
    }

}
