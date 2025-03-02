package com.stapp.sporttrack.data.interfaces.exercice

import com.google.android.gms.maps.model.LatLng
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import java.util.Locale

class RunningStrategy : ExerciseStrategy {
    override fun displayMap(): Boolean = true
    override fun calculateSteps(): Boolean = false
    override fun supportsAutoPause(): Boolean = true
    override fun getTitle() = R.string.exercice_runing
    override fun getIcon() = R.drawable.ic_run_marker
    override fun getImage() = R.drawable.exercise_running_image
    private val metValue = 7.5 // MET pour la course

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

        stats.totalDuration =
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                totalDurationInSeconds / 60,
                totalDurationInSeconds % 60
            )
        val distanceInKm = totalDistance / 1000
        stats.totalDistance =
            if (distanceInKm < 0.01) 0.0 else Math.round(distanceInKm * 100.0) / 100.0

        val speed = if (totalDurationInSeconds > 0) {
            (stats.totalDistance / (totalDurationInSeconds / 3600.0)) // km/h
        } else {
            0.0
        }
        stats.averageSpeed = Math.round(speed * 100.0) / 100.0

        stats.cadence = if (totalDurationInSeconds > 0) {
            stepCount / (totalDurationInSeconds / 60.0) // Pas par minute
        } else {
            0.0
        }
        stats.rhythm = if (stats.totalDistance > 0) {
            totalDurationInSeconds / stats.totalDistance // Temps par km
        } else {
            0.0
        }
        stats.calories = calculateCalories(elapsedTime)

        return stats
    }

    override fun getSummaryCardsData(stats: ExerciseStats): List<ExerciseSummaryCardData> {
        return listOf(
            ExerciseSummaryCardData(title = "Durée", value = stats.totalDuration),
            ExerciseSummaryCardData(
                title = "Cadence",
                value = stats.cadence.toString(),
                unit = "ppm"
            ),
            ExerciseSummaryCardData(
                title = "Rythme",
                value = stats.rhythm.toString(),
                unit = "/km"
            ),
            ExerciseSummaryCardData(
                title = "Distance",
                value = stats.totalDistance.toString(),
                unit = "Km"
            )
        )
    }
}