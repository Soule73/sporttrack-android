package com.stapp.sporttrack.data.interfaces

import com.stapp.sporttrack.data.models.LoginRequest
import com.stapp.sporttrack.data.models.AuthResponse
import com.stapp.sporttrack.data.models.UserRequest
import com.stapp.sporttrack.data.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {

    @POST("auth/register")
    suspend fun register(@Body userRequest: UserRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @GET("auth/users/me")
    suspend fun getCurrentUser(): Response<UserResponse>
}

