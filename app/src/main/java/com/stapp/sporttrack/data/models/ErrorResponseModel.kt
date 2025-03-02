package com.stapp.sporttrack.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val errors: Map<String, String>
)
