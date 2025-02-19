package com.stapp.sporttrack.utils

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord

object SharedPreferencesConstants {
    const val PREF_NAME = "AppPreferences"
    const val AUTH_TOKEN = "AUTH_TOKEN"
    const val USER_DATA = "USER_DATA"
    const val IS_FIRST_OPEN = "IS_FIRST_OPEN"
}

val PERMISSIONS  = setOf(
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getWritePermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getWritePermission(StepsRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class),
    HealthPermission.getWritePermission(ExerciseSessionRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(WeightRecord::class),
    HealthPermission.getWritePermission(WeightRecord::class),
)