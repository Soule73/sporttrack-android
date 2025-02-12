package com.stapp.sporttrack.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)
