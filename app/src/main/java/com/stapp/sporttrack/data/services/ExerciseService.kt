package com.stapp.sporttrack.data.services

import com.stapp.sporttrack.data.models.AddExerciseSessionRequest
import com.stapp.sporttrack.data.models.DailyExerciseDetailsResponse
import com.stapp.sporttrack.data.models.DailyExerciseStatsResponse
import com.stapp.sporttrack.data.models.ExerciseSessionResponse
import com.stapp.sporttrack.data.models.WeeklyExerciseStatsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExerciseService {

    @POST("exercises/add")
    suspend fun addExerciseSession(@Body exerciseRequest: AddExerciseSessionRequest): Response<Map<String, Int>>

    @GET("exercises/user")
    suspend fun getExerciseSessionsByUser(): Response<List<ExerciseSessionResponse>>

    @GET("/exercises/stats/daily")
    suspend fun getDailyExerciseStats(): Response<List<DailyExerciseStatsResponse>>

    @GET("/exercises/stats/daily/details/{date}")
    suspend fun getDailyExerciseDetails(@Path("date") date: String): Response<DailyExerciseDetailsResponse>

    @GET("/exercises/stats/weekly")
    suspend fun getWeeklyExerciseStats(): Response<WeeklyExerciseStatsResponse>

    @GET("/exercises/{sessionId}")
    suspend fun getExerciseSessionById(@Path("sessionId") sessionId: Int): Response<ExerciseSessionResponse>

}