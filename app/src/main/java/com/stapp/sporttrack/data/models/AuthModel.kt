package com.stapp.sporttrack.data.models

import com.stapp.sporttrack.data.interfaces.HasBirthDate
import com.stapp.sporttrack.data.interfaces.HasEmail
import com.stapp.sporttrack.data.interfaces.HasGender
import com.stapp.sporttrack.data.interfaces.HasHeight
import com.stapp.sporttrack.data.interfaces.HasName
import com.stapp.sporttrack.data.interfaces.HasPassword
import com.stapp.sporttrack.data.interfaces.HasWeight
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class UserRequest(
    override val email: String,
    override val password: String,
    override val firstName: String,
    override val lastName: String,
    override val birthDate: String? = null,
    override val gender: String? = null,
    override val weight: Double? = null,
    override val height: Double? = null
) : HasEmail, HasPassword, HasName, HasBirthDate, HasGender, HasWeight, HasHeight

@Serializable
data class UserUpdateRequest(
    override val email: String,
    override val firstName: String,
    override val lastName: String,
    override val birthDate: String? = null,
    override val gender: String? = null,
    override val weight: Double? = null,
    override val height: Double? = null
) : HasEmail, HasName, HasBirthDate, HasGender, HasWeight, HasHeight

@Serializable
data class UserResponse(
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String? = null,
    val gender: String? = null,
    val weight: Double? = null,
    val height: Double? = null,
    val registrationDate: String? = null
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

@Serializable
data class DeleteAccountRequest(
    val password: String
)
