package com.stapp.sporttrack.data.interfaces.exercice

import com.google.android.gms.maps.model.LatLng
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import java.util.Locale

class SwimmingStrategy : ExerciseStrategy {
    override fun displayMap(): Boolean = false
    override fun calculateSteps(): Boolean = false
    override fun supportsAutoPause(): Boolean = false
    override fun getTitle() = R.string.exercise_swimming
    override fun getIcon() = R.drawable.ic_swimminng_marker
    override fun getImage() = R.drawable.exercise_swimming_image
    private val metValue = 7.0 // MET pour la natation

    override fun calculateCalories(elapsedTime: Long, weightInKg: Double): Double {
        val durationInSeconds = elapsedTime / 1000
        val calories = (metValue * (3.5 / 60) * weightInKg * (durationInSeconds) / 200)
        return Math.round(calories * 100.0) / 100.0

    }

    override fun calculateIncline(startLocation: LatLng?, endLocation: LatLng?): Double {
        return 0.0
    }

    override fun updateExerciseStats(
        weightInKg: Double?,
        elapsedTime: Long,
        totalDistance: Double,
        stepCount: Int,
        lastLocation: LatLng?,
        newLocation: LatLng?
    ): ExerciseStats {
        val stats = ExerciseStats()
        val totalDurationInSeconds = elapsedTime / 1000

        stats.totalDuration = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            totalDurationInSeconds / 60,
            totalDurationInSeconds % 60
        )
        stats.calories = calculateCalories(elapsedTime)

        return stats
    }

    override fun getSummaryCardsData(stats: ExerciseStats): List<ExerciseSummaryCardData> {
        return listOf(
            ExerciseSummaryCardData(title = "Durée", value = stats.totalDuration),
            ExerciseSummaryCardData(
                title = "Calories",
                value = stats.calories.toString(),
                unit = "Kcal"
            )
        )
    }
}