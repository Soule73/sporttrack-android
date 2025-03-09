package com.stapp.sporttrack.data.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
@Serializable
data class ExerciseStats(
    var totalDuration: String = "00:00",
    var totalDistance: Double = 0.0,
    var averageSpeed: Double = 0.0, // Vitesse moyenne en km/h
    var stepCount: Int = 0,
    var cadence: Double = 0.0, // Cadence moyenne en pas/min
    var rhythm: Double = 0.0, // Rythme cardiaque en /km
    var slope: Double = 0.0, // Dénivelé(pente) en %
    var calories: Double = 0.0, // Calories brûlées en kcal
    )

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
    val stepCount: Int? = null,
    val cadence: Double? = null, // /min
    val rhythm: Double? = null, // /km
    val slope: Double? = null, // En %
    val comment: String? = null,
    val status: String = "private",
    val isAuto: Boolean = false,
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
    val status: String,
    val isAuto: Boolean
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

//object SharedExerciseState {
//    var userId: Int? = null
//    var isSessionActive: Boolean = false
//    var isSessionPaused: Boolean = false
//    var isAutoPaused: Boolean = false
//    var elapsedTime: Long = 0L
//    var totalDistance: Double = 0.0
//    var stepCount: Int = 0
//    var lastLocation: LatLng? = null
//    var stats: ExerciseStats = ExerciseStats()
//    var exerciseType: Int = ExerciseSessionRecord.EXERCISE_TYPE_WALKING
//    var sessionStartTime: Long = 0L
//    var startTime: Long = 0L
//    var sessionEndTime: Long = 0L
//
//    fun reset() {
//        isSessionActive = false
//        isSessionPaused = false
//        isAutoPaused = false
//        elapsedTime = 0L
//        totalDistance = 0.0
//        stepCount = 0
//        lastLocation = null
//        stats = ExerciseStats()
//        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WALKING
//        startTime = 0L
//        sessionEndTime = 0L
//        sessionStartTime = 0L
//    }
//}


object SharedExerciseState {
    var userId: Int? by mutableStateOf(null)
    var userWeight: Double? by mutableStateOf(null)
    var isSessionActive by mutableStateOf(false)
    var isSessionPaused by mutableStateOf(false)
    var isAutoPaused by mutableStateOf(false)
    var elapsedTime by mutableLongStateOf(0L)
    var totalDistance by mutableDoubleStateOf(0.0)
    var stepCount by mutableIntStateOf(0)
    var lastLocation: LatLng? by mutableStateOf(null)
    var stats: ExerciseStats by mutableStateOf(ExerciseStats())
    var exerciseType by mutableIntStateOf(ExerciseSessionRecord.EXERCISE_TYPE_WALKING)
    var sessionStartTime by mutableLongStateOf(0L)
    var startTime by mutableLongStateOf(0L)
    var sessionEndTime by mutableLongStateOf(0L)

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
        sessionStartTime = 0L
        sessionEndTime = 0L
//        userId = null
    }
}
