package com.stapp.sporttrack.data.repository

import android.content.SharedPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.stapp.sporttrack.BuildConfig
import com.stapp.sporttrack.data.services.AuthInterceptor
import com.stapp.sporttrack.data.services.ExerciseService
import com.stapp.sporttrack.data.models.AddExerciseSessionRequest
import com.stapp.sporttrack.data.models.DailyExerciseDetailsResponse
import com.stapp.sporttrack.data.models.DailyExerciseStatsResponse
import com.stapp.sporttrack.data.models.ErrorResponse
import com.stapp.sporttrack.data.models.ExerciseSessionResponse
import com.stapp.sporttrack.data.models.WeeklyExerciseStatsResponse
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalSerializationApi::class)
class ExerciseRepository(private val sharedPreferences: SharedPreferences) {

    private val exerciseService: ExerciseService
    private val json = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(LocalDateTimeSerializer)
            contextual(LocalDateSerializer)
        }
    }

    init {
        val contentType = "application/json".toMediaType()
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { getToken() })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        exerciseService = retrofit.create(ExerciseService::class.java)
    }

    private fun getToken(): String? {
        return sharedPreferences.getString(SharedPreferencesConstants.AUTH_TOKEN, null)
    }

    suspend fun addExerciseSession(exerciseRequest: AddExerciseSessionRequest): Result<Int> {
        return try {
            val response = exerciseService.addExerciseSession(exerciseRequest)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.containsKey("sessionId")) {
                    Result.success(body["sessionId"]!!)
                } else {
                    Result.failure(Exception("Réponse du serveur invalide"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.failure(CustomException(errorResponse))
                } else {
                    Result.failure(CustomException(ErrorResponse(mapOf("error" to "Erreur inconnue lors de l'ajout de la session d'exercice"))))
                }
            }
        } catch (e: Exception) {
            val errorResponse =
                ErrorResponse(errors = mapOf("error" to (e.message ?: "Erreur inconnue")))
            Result.failure(CustomException(errorResponse))
        }
    }

    suspend fun getExerciseSessionsByUser(): Result<List<ExerciseSessionResponse>> {
        return try {
            val response = exerciseService.getExerciseSessionsByUser()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Réponse du serveur vide"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.failure(CustomException(errorResponse))
                } else {
                    Result.failure(CustomException(ErrorResponse(mapOf("error" to "Erreur inconnue lors de la récupération des sessions d'exercice"))))
                }
            }
        } catch (e: Exception) {
            val errorResponse =
                ErrorResponse(errors = mapOf("error" to (e.message ?: "Erreur inconnue")))
            Result.failure(CustomException(errorResponse))
        }
    }

    suspend fun getDailyExerciseStats(): Result<List<DailyExerciseStatsResponse>> {
        return try {
            val response = exerciseService.getDailyExerciseStats()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Réponse du serveur vide"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.failure(CustomException(errorResponse))
                } else {
                    Result.failure(CustomException(ErrorResponse(mapOf("error" to "Erreur inconnue lors de la récupération des statistiques d'exercice"))))
                }
            }
        } catch (e: Exception) {
            val errorResponse =
                ErrorResponse(errors = mapOf("error" to (e.message ?: "Erreur inconnue")))
            Result.failure(CustomException(errorResponse))
        }
    }

    suspend fun getDailyExerciseDetails(date: LocalDate): Result<DailyExerciseDetailsResponse> {
        return try {
            val response = exerciseService.getDailyExerciseDetails(date.toString())
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Réponse du serveur vide"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.failure(CustomException(errorResponse))
                } else {
                    Result.failure(CustomException(ErrorResponse(mapOf("error" to "Erreur inconnue lors de la récupération des détails des exercices quotidiens"))))
                }
            }
        } catch (e: Exception) {
            val errorResponse =
                ErrorResponse(errors = mapOf("error" to (e.message ?: "Erreur inconnue")))
            Result.failure(CustomException(errorResponse))
        }
    }

    suspend fun getWeeklyExerciseStats(): Result<WeeklyExerciseStatsResponse> {
        return try {
            val response = exerciseService.getWeeklyExerciseStats()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Réponse du serveur vide"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.failure(CustomException(errorResponse))
                } else {
                    Result.failure(CustomException(ErrorResponse(mapOf("error" to "Erreur inconnue lors de la récupération des statistiques hebdomadaires"))))
                }
            }
        } catch (e: Exception) {
            val errorResponse =
                ErrorResponse(errors = mapOf("error" to (e.message ?: "Erreur inconnue")))
            Result.failure(CustomException(errorResponse))
        }
    }

    suspend fun getExerciseSessionById(sessionId: Int): Result<ExerciseSessionResponse> {
        return try {
            val response = exerciseService.getExerciseSessionById(sessionId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty server response"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Json.decodeFromString<ErrorResponse>(errorBody)
                    Result.failure(CustomException(errorResponse))
                } else {
                    Result.failure(CustomException(ErrorResponse(mapOf("error" to "Unknown error while fetching the session"))))
                }
            }
        } catch (e: Exception) {
            val errorResponse = ErrorResponse(errors = mapOf("error" to (e.message ?: "Unknown error")))
            Result.failure(CustomException(errorResponse))
        }
    }
}


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}