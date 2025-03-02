package com.stapp.sporttrack.data.interfaces.exercice

import com.google.android.gms.maps.model.LatLng
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import com.stapp.sporttrack.utils.distanceTo
import java.util.Locale

class CyclingStrategy : ExerciseStrategy {
    override fun displayMap(): Boolean = true
    override fun calculateSteps(): Boolean = false
    override fun supportsAutoPause(): Boolean = true
    override fun getTitle() = R.string.exercise_cycling
    override fun getIcon() = R.drawable.ic_bike_marker
    override fun getImage() = R.drawable.exercise_cycling_image
    private val metValue = 6.8 // MET pour le cyclisme

    override fun calculateCalories(elapsedTime: Long, weightInKg: Double): Double {
        val durationInSeconds = elapsedTime / 1000
        val calories = (metValue * (3.5 / 60) * weightInKg * (durationInSeconds) / 200)
        return Math.round(calories * 100.0) / 100.0
    }

    override fun calculateIncline(startLocation: LatLng?, endLocation: LatLng?): Double {
        if (startLocation == null || endLocation == null) return 0.0

        val altitudeDifference = endLocation.latitude - startLocation.latitude
        val horizontalDistance = startLocation.distanceTo(endLocation)
        if (horizontalDistance == 0.0) return 0.0

        return (altitudeDifference / horizontalDistance) * 100
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
        val distanceInKm = totalDistance / 1000
        stats.totalDistance = if (distanceInKm < 0.01) 0.0 else {
            (Math.round(distanceInKm * 100.0) / 100.0)
        }

        stats.averageSpeed = if (totalDurationInSeconds > 0) {
            val speed = stats.totalDistance / (totalDurationInSeconds / 3600.0) // km/h
            Math.round(speed * 100.0) / 100.0
        } else {
            0.0
        }

        stats.slope = calculateIncline(lastLocation, newLocation)
        stats.calories = calculateCalories(elapsedTime)

        return stats
    }

    override fun getSummaryCardsData(stats: ExerciseStats): List<ExerciseSummaryCardData> {
        return listOf(
            ExerciseSummaryCardData(title = "Durée", value = stats.totalDuration),
            ExerciseSummaryCardData(title = "Pente", value = stats.slope.toString(), unit = "%"),
            ExerciseSummaryCardData(
                title = "Vitesse",
                value = stats.averageSpeed.toString(),
                unit = "Km/h"
            ),
            ExerciseSummaryCardData(
                title = "Distance",
                value = stats.totalDistance.toString(),
                unit = "Km"
            )
        )
    }
}