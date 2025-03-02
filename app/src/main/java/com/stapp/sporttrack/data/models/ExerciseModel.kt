package com.stapp.sporttrack.data.models

import androidx.health.connect.client.records.ExerciseSessionRecord
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class DailyExerciseStatsResponse(
    @Contextual val date: LocalDate,
    val hasExercise: Boolean
)

object ExerciseStatsUnit {
    const val DISTANCE = "Km"
    const val MIN = "min"
    const val SEC = "s"
    const val SPEED = "Km/h"
    const val CALORIES = "Kcal"
    const val RHYTHM = "/Km"
    const val CADENCE = "ppm"
    const val SLOPE = "%"

}
data class ExerciseSummaryCardData(
    val title: String,
    val value: String,
    val unit: String? = "",
    val icon: Int? = null,
    val color: androidx.compose.ui.graphics.Color? = null,
)

@Serializable
data class DailyExerciseDetailsResponse(
    val totalDuration: Int, // En secondes
    val totalDistance: Double, // En kilomètres
    val totalCalories: Double, // En kcal
    val totalSteps: Int,
    val totalCadence: Double,
    val totalRhythm: Double,
    val totalSlope: Double,
    val sessions: List<ExerciseSessionResponse>
)

data class ExerciseStats(
    var totalDuration: String = "00:00",
    var totalDistance: Double = 0.0,
    var averageSpeed: Double = 0.0, // Vitesse moyenne en km/h
    var stepCount: Int = 0,
    var cadence: Double = 0.0, // Cadence moyenne en pas/min
    var rhythm: Double = 0.0, // Rythme cardiaque en /km
    var slope: Double = 0.0, // Dénivelé(pente) en %
    var calories: Double = 0.0, // Calories brûlées en kcal

    ) : java.io.Serializable

@Serializable
data class AddExerciseSessionRequest(
    val userId: Int,
    val activityTypeId: Int,
    @Contextual val startDate: LocalDateTime,
    @Contextual val endDate: LocalDateTime? = null,
    val duration: Int? = null,
    val distance: Double? = null, // En kilomètres
    val caloriesBurned: Double? = null, // En kcal
    val averageSpeed: Double? = null, // En km/h
    val stepCount: Int? = null, // En pas
    val cadence: Double? = null, // En pas/min
    val rhythm: Double? = null, // En pas/km
    val slope: Double? = null, // En %
    val comment: String? = null,
    val status: String = "private"
)

@Serializable
data class ExerciseSessionResponse(
    val sessionId: Int,
    val userId: Int,
    val activityTypeId: Int,
    @Contextual val startDate: LocalDateTime,
    @Contextual val endDate: LocalDateTime? = null,
    val duration: Int?,
    val distance: Double?,
    val caloriesBurned: Double?,
    val averageSpeed: Double?,
    val stepCount: Int?,
    val cadence: Double?,
    val rhythm: Double?,
    val slope: Double?,
    val comment: String?,
    val status: String
)

@Serializable
data class WeeklyExerciseStatsResponse(
    val totalDuration: Int,
    val totalDistance: Double,
    val totalCalories: Double,
    val totalSteps: Int,
    val totalCadence: Double,
    val totalRhythm: Double,
    val totalSlope: Double
)

object SharedExerciseState {
    var userId: Int? = null
    var isSessionActive: Boolean = false
    var isSessionPaused: Boolean = false
    var isAutoPaused: Boolean = false
    var elapsedTime: Long = 0L
    var totalDistance: Double = 0.0
    var stepCount: Int = 0
    var lastLocation: LatLng? = null
    var stats: ExerciseStats = ExerciseStats()
    var exerciseType: Int = ExerciseSessionRecord.EXERCISE_TYPE_WALKING
    var sessionStartTime: Long = 0L
    var startTime: Long = 0L
    var sessionEndTime: Long = 0L

    fun reset() {
        isSessionActive = false
        isSessionPaused = false
        isAutoPaused = false
        elapsedTime = 0L
        totalDistance = 0.0
        stepCount = 0
        lastLocation = null
        stats = ExerciseStats()
        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WALKING
        startTime = 0L
        sessionEndTime = 0L
        sessionStartTime = 0L
    }
}