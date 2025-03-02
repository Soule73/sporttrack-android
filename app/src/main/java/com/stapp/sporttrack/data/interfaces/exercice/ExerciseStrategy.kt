package com.stapp.sporttrack.data.interfaces.exercice

import com.google.android.gms.maps.model.LatLng
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData

interface ExerciseStrategy {
    fun displayMap(): Boolean
    fun calculateSteps(): Boolean
    fun supportsAutoPause(): Boolean
    fun calculateCalories(elapsedTime: Long, weightInKg: Double = 70.0): Double
    fun calculateIncline(startLocation: LatLng?, endLocation: LatLng?): Double
    fun updateExerciseStats(
        weightInKg: Double? = 70.0,
        elapsedTime: Long,
        totalDistance: Double,
        stepCount: Int,
        lastLocation: LatLng?,
        newLocation: LatLng?
    ): ExerciseStats

    fun getSummaryCardsData(stats: ExerciseStats): List<ExerciseSummaryCardData>
    fun getTitle(): Int
    fun getIcon(): Int
    fun getImage(): Int
}