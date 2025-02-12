package com.stapp.sporttrack.data.models

import kotlinx.serialization.Serializable

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
