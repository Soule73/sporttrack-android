package com.stapp.sporttrack.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stapp.sporttrack.data.models.AuthResponse
import com.stapp.sporttrack.data.models.LoginRequest
import com.stapp.sporttrack.data.models.UserRequest
import com.stapp.sporttrack.data.models.UserResponse
import com.stapp.sporttrack.data.models.UserUpdateRequest
import com.stapp.sporttrack.data.repository.AuthRepository
import com.stapp.sporttrack.data.repository.CustomException
import com.stapp.sporttrack.utils.convertDateToMillis
import com.stapp.sporttrack.utils.AuthUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@SuppressLint("StaticFieldLeak")
class AuthViewModel(context: Context, private val authRepository: AuthRepository) : ViewModel() {

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> = _lastName

    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> = _firstName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _height = MutableStateFlow(0f)
    val height: StateFlow<Float> = _height

    private val _weight = MutableStateFlow(0f)
    val weight: StateFlow<Float> = _weight

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _dateOfBirth = MutableStateFlow<String?>(null)
    val dateOfBirth: StateFlow<String?> = _dateOfBirth

    private val _selectedDate = MutableStateFlow<Long?>(null)
    val selectedDate: StateFlow<Long?> = _selectedDate

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _isFirstOpen = MutableStateFlow(false)
    val isFirstOpen: StateFlow<Boolean> = _isFirstOpen

    private val _registrationResult = MutableStateFlow<Result<AuthResponse>?>(null)
    var registrationResult: StateFlow<Result<AuthResponse>?> = _registrationResult

    private val _updateResult = MutableStateFlow<Result<UserResponse>?>(null)
    var updateResult: StateFlow<Result<UserResponse>?> = _updateResult

    private val _loginResult = MutableStateFlow<Result<AuthResponse>?>(null)
    var loginResult: StateFlow<Result<AuthResponse>?> = _loginResult

    private val _verifyTokenResult = MutableStateFlow<Result<UserResponse>?>(null)
    val verifyTokenResult: StateFlow<Result<UserResponse>?> = _verifyTokenResult

    private val _registrationError = MutableStateFlow<Map<String, String>?>(null)
    var registrationError: StateFlow<Map<String, String>?> = _registrationError

    private val _userData = MutableStateFlow<UserResponse?>(null)
    var userData: StateFlow<UserResponse?> = _userData

    init {
        AuthUtils.getUserData(context)?.let { data ->
            setUserData(data)
        }
    }

    fun setHeight(newHeight: Float) {
        _height.value = newHeight
    }

    fun setLastName(newLastName: String) {
        _lastName.value = newLastName
    }

    fun setFirstName(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun setPassword(newPassword: String) {
        _password.value = newPassword
    }

    fun setWeight(newWeight: Float) {
        _weight.value = newWeight
    }

    fun setGender(newGender: String) {
        _gender.value = newGender
    }

    fun setUserData(userResponse: UserResponse) {
        _userData.value = userResponse
    }

    fun setDateOfBirth(date: String?) {
        _dateOfBirth.value = date
    }

    fun setSelectedDate(date: Long?) {
        _selectedDate.value = date
    }

    fun resetData() {


        setEmail("")
        setFirstName("")
        setLastName("")
        setGender("")
        setHeight(0f)
        setWeight(0f)
        setPassword("")

        _registrationError.value = null
        _registrationResult.value = null
        _updateResult.value = null
        _loginResult.value = null

        _isAuthenticated.value=false
        _userData.value=null
    }

    fun beginUpdate() {
        val data = userData.value

        setGender(data?.gender.orEmpty())
        setEmail(data?.email.orEmpty())
        setFirstName(data?.firstName.orEmpty())
        setLastName(data?.lastName.orEmpty())
        data?.height?.let { setHeight(it.toFloat()) }
        data?.weight?.let { setWeight(it.toFloat()) }

        data?.birthDate?.let {
            _selectedDate.value = convertDateToMillis(it)
        }
    }

    private fun buildUserRequest(): UserRequest {
        return UserRequest(
            email = email.value,
            password = password.value,
            firstName = firstName.value,
            lastName = lastName.value,
            birthDate = null,
            gender = gender.value,
            weight = weight.value.takeIf { it > 0f }?.toDouble(),
            height = height.value.takeIf { it > 0f }?.toDouble(),
        )
    }
    fun register() {
        viewModelScope.launch {
            val userRequest = buildUserRequest()
            val result = authRepository.register(userRequest)

            if (result.isSuccess) {
                _registrationResult.value = result
                _userData.value = result.getOrNull()?.user
                _registrationError.value = null
            } else {
                val error = result.exceptionOrNull()
                if (error is CustomException) {
                    _registrationError.value = parseErrorMessages(error)
                } else {
                    _registrationError.value = mapOf("error" to "Erreur inconnue")
                }
            }

        }

    }
    private fun buildUserUpdateRequest(): UserUpdateRequest {
        return UserUpdateRequest(
            email = email.value,
            firstName = firstName.value,
            lastName = lastName.value,
            birthDate = dateOfBirth.value,
            gender = gender.value,
            weight = weight.value.takeIf { it > 0f }?.toDouble(),
            height = height.value.takeIf { it > 0f }?.toDouble(),
        )
    }

       fun update() {
        viewModelScope.launch {
            val userUpdateRequest = buildUserUpdateRequest()
            val result = authRepository.update(userUpdateRequest)
            if (result.isSuccess) {
                _userData.update {
                    result.getOrNull()
                }
                _updateResult.value = result
                _registrationError.value = null

            } else {
                val error = result.exceptionOrNull()
                if (error is CustomException) {
                    _registrationError.value = parseErrorMessages(error)
                } else {
                    _registrationError.value = mapOf("error" to "Erreur inconnue")
                }
            }

        }

    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val loginRequest = LoginRequest(email, password)

            println("loginRequest: $loginRequest")
            val result = authRepository.login(loginRequest)
            _loginResult.value = result
        }
    }


    fun logout() {
        resetData()

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
    fun setIsAuthenticated(context: Context) {
        _isAuthenticated.value=AuthUtils.checkAuthentication(context)
    }

    fun setIsFirstOpen(context: Context) {
        _isFirstOpen.value=AuthUtils.checkIfIsFirstOpen(context)
    }
}

