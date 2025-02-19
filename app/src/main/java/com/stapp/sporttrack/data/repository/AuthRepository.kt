package com.stapp.sporttrack.data.repository

import android.content.SharedPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stapp.sporttrack.BuildConfig
import com.stapp.sporttrack.data.AuthInterceptor
import com.stapp.sporttrack.data.interfaces.AuthService
import com.stapp.sporttrack.data.models.AuthResponse
import com.stapp.sporttrack.data.models.ErrorResponse
import com.stapp.sporttrack.data.models.LoginRequest
import com.stapp.sporttrack.data.models.UserRequest
import com.stapp.sporttrack.data.models.UserResponse
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Suppress("JSON_FORMAT_REDUNDANT")
@OptIn(ExperimentalSerializationApi::class)
class AuthRepository(private val sharedPreferences: SharedPreferences) {

    private val authService: AuthService

    init {
        val contentType = "application/json".toMediaType()
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { getToken() })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(contentType))
            .build()

        authService = retrofit.create(AuthService::class.java)
    }

    private fun getToken(): String? {
        return sharedPreferences.getString(SharedPreferencesConstants.AUTH_TOKEN, null)
    }

    suspend fun register(userRequest: UserRequest): Result<AuthResponse> {
        return try {
            val response = authService.register(userRequest)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    // Réponse du serveur vide
                    Result.failure(Exception("Réponse du serveur vide"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.failure(CustomException(errorResponse)) // Utilisation de CustomException pour encapsuler ErrorResponse
                } else {
                    // Erreur inconnue lors de l'inscription
                    Result.failure(CustomException(ErrorResponse(mapOf("error" to "Erreur inconnue lors de l'inscription"))))
                }
            }
        } catch (e: Exception) {
            // Créer un ErrorResponse pour les exceptions inattendues
            val errorResponse =
                ErrorResponse(errors = mapOf("error" to (e.message ?: "Erreur inconnue")))
            Result.failure(CustomException(errorResponse))
        }
    }

    suspend fun login(loginRequest: LoginRequest): Result<AuthResponse> {
        return try {
            val response = authService.login(loginRequest)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Réponse du serveur vide."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Erreur inconnue lors de la connexion."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyToken(): Result<UserResponse> {
        return try {
            val response = authService.getCurrentUser()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    logout()
                    Result.failure(Exception("Réponse du serveur vide."))
                }
            } else {
                logout()
                val errorBody = response.errorBody()?.string()
                Result.failure(
                    Exception(
                        errorBody ?: "Erreur inconnue lors de la vérification du token."
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            remove(SharedPreferencesConstants.AUTH_TOKEN)
            remove(SharedPreferencesConstants.USER_DATA)
            apply()
        }
    }
}

// CustomException pour encapsuler ErrorResponse
class CustomException(val errorResponse: ErrorResponse) : Exception()