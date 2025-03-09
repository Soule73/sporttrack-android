package com.stapp.sporttrack.data.interfaces.exercice

import com.google.android.gms.maps.model.LatLng
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import java.util.Locale

class WalkingStrategy : ExerciseStrategy {
    override fun displayMap(): Boolean = true
    override fun calculateSteps(): Boolean = true
    override fun supportsAutoPause(): Boolean = true
    override fun getTitle() = R.string.exercie_walking
    override fun getIcon() = R.drawable.ic_directions_walk
    override fun getImage() = R.drawable.exercise_walking_image
    private val metValue = 3.8 // MET pour la marche

    private val averageStepLength = 0.75

    override fun calculateCalories(elapsedTime: Long, weightInKg: Double): Double {
        //val durationInMinutes = elapsedTime / 60000
        //val calories = (metValue * 3.5 * weightInKg * durationInMinutes / 200)
        //return Math.round(calories * 100.0) / 100.0

        val durationInSeconds = elapsedTime / 1000.0
        val calories = (metValue * (3.5 / 60) * weightInKg * durationInSeconds / 200)
        return (calories * 100) / 100.0
    }

    override fun calculateIncline(startLocation: LatLng?, endLocation: LatLng?) = 0.0

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

        stats.totalDuration =
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                totalDurationInSeconds / 60,
                totalDurationInSeconds % 60
            )

        // Calcul de la distance basée sur le nombre de pas et la longueur de foulée moyenne
        val distanceFromStepsInMeters = stepCount * averageStepLength
        val distanceInKm = distanceFromStepsInMeters / 1000.0
        stats.totalDistance =
            if (distanceInKm < 0.01) 0.0 else (Math.round(distanceInKm * 100.0) / 100.0)
        // Calcul de la vitesse moyenne basée sur la distance obtenue via les pas
        stats.averageSpeed = if (totalDurationInSeconds > 0) {
            val speed = (distanceFromStepsInMeters / 1000.0) / (totalDurationInSeconds / 3600.0)
            (Math.round(speed * 100.0) / 100.0)
        } else 0.0
        stats.stepCount = stepCount
        stats.calories = calculateCalories(elapsedTime, weightInKg ?: 70.0)

        return stats
    }

    override fun getSummaryCardsData(stats: ExerciseStats): List<ExerciseSummaryCardData> {
        return listOf(
            ExerciseSummaryCardData(title = "Durée", value = stats.totalDuration),
            ExerciseSummaryCardData(
                title = "Distance",
                value = stats.totalDistance.toString(),
                unit = "Km"
            ),
            ExerciseSummaryCardData(
                title = "Vitesse",
                value = stats.averageSpeed.toString(),
                unit = "Km/h"
            ),
            ExerciseSummaryCardData(title = "Pas", value = stats.stepCount.toString())
        )
    }

}