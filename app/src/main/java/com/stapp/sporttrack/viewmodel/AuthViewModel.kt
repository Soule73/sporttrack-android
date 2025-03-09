package com.stapp.sporttrack.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stapp.sporttrack.data.models.AuthResponse
import com.stapp.sporttrack.data.models.ChangePasswordRequest
import com.stapp.sporttrack.data.models.LoginRequest
import com.stapp.sporttrack.data.models.SharedExerciseState
import com.stapp.sporttrack.data.models.UserRequest
import com.stapp.sporttrack.data.models.UserResponse
import com.stapp.sporttrack.data.models.UserUpdateRequest
import com.stapp.sporttrack.data.repository.AuthRepository
import com.stapp.sporttrack.data.repository.CustomException
import com.stapp.sporttrack.utils.AuthUtils
import com.stapp.sporttrack.utils.convertDateToMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


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

    private val _deleteAccountResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteAccountResult: StateFlow<Result<Unit>?> = _deleteAccountResult

    private val _changePasswordResult = MutableStateFlow<Result<Unit>?>(null)
    val changePasswordResult: StateFlow<Result<Unit>?> = _changePasswordResult

    init {
        _isAuthenticated.value = AuthUtils.checkAuthentication(context)
        _isFirstOpen.value = AuthUtils.checkIfIsFirstOpen(context)
        val data = AuthUtils.getUserData(context)
        if (data != null) {
            setUserData(data)
            SharedExerciseState.userId=data.userId
            SharedExerciseState.userWeight=data.weight

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

    fun register() {
        viewModelScope.launch {
            val userRequest = UserRequest(
                email = email.value,
                password = password.value,
                firstName = firstName.value,
                lastName = lastName.value,
                birthDate = null, // Optional
                gender = gender.value,
                weight = if (weight.value.toDouble() > 0) weight.value.toDouble() else null,
                height = if (height.value.toDouble() > 0) height.value.toDouble() else null,
            )

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

    fun update() {
        viewModelScope.launch {
            val userUpdateRequest = UserUpdateRequest(
                email = email.value,
                firstName = firstName.value,
                lastName = lastName.value,
                birthDate = dateOfBirth.value,
                gender = gender.value,
                weight = if (weight.value.toDouble() > 0) weight.value.toDouble() else null,
                height = if (height.value.toDouble() > 0) height.value.toDouble() else null,
            )

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

    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            println("currentPassword: $currentPassword")
            println("newPassword: $newPassword")
            val request =
                ChangePasswordRequest(currentPassword = currentPassword, newPassword = newPassword)

            println("request: $request")
            _changePasswordResult.value = authRepository.changePassword(request)
        }
    }

    fun resetChangePasswordResult() {
        _changePasswordResult.value = null
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _deleteAccountResult.value = authRepository.deleteAccount(password)
        }
    }

    fun resetDeleteAccountResult() {
        _deleteAccountResult.value = null
    }

    fun logout() {
        _isAuthenticated.value = false
        _userData.value = null
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

}
