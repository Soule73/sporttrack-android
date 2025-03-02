package com.stapp.sporttrack.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.interfaces.exercice.CyclingStrategy
import com.stapp.sporttrack.data.interfaces.exercice.ExerciseStrategy
import com.stapp.sporttrack.data.interfaces.exercice.RunningStrategy
import com.stapp.sporttrack.data.interfaces.exercice.StrengthTrainingStrategy
import com.stapp.sporttrack.data.interfaces.exercice.SwimmingStrategy
import com.stapp.sporttrack.data.interfaces.exercice.WalkingStrategy
import com.stapp.sporttrack.data.interfaces.exercice.YogaStrategy
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * `ExerciseUtils` is a utility object that provides helper functions for managing exercise-related operations.
 * It includes functionality for determining exercise strategies, managing map properties, handling permissions,
 * creating location flows, and managing step detection.
 */
object ExerciseUtils {

    fun getExerciseStrategy(exerciseType: Int): ExerciseStrategy {
        return when (exerciseType) {
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> WalkingStrategy()
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> RunningStrategy()
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> CyclingStrategy()
            ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> StrengthTrainingStrategy()
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> SwimmingStrategy()
            ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> YogaStrategy()
            else -> WalkingStrategy()
        }
    }

    fun getMapProperties(context: Context, isDarkMode: Boolean = false): MapProperties {
        return MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL,
            mapStyleOptions = if (isDarkMode) MapStyleOptions.loadRawResourceStyle(
                context,
                R.raw.dark_maps_raw
            ) else null
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getPermissionsList(exerciseStrategy: ExerciseStrategy): List<String> {
        val permissions = mutableListOf(Manifest.permission.ACTIVITY_RECOGNITION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (exerciseStrategy.displayMap()) {
            permissions.addAll(
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
        return permissions
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun requestPermissions(permissionsState: MultiplePermissionsState) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    fun createLocationFlow(
        context:Context,
        fusedLocationClient: FusedLocationProviderClient
    ): Flow<LatLng?>? {
        val permissionsGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionsGranted) {

            return null
        }
        return callbackFlow {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        trySend(LatLng(location.latitude, location.longitude))
                    }
                }
            }

            val locationRequest = LocationRequest.Builder(1000L)
                .setMinUpdateIntervalMillis(1000L)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun manageStepDetector(
        context: Context,
        exerciseViewModel: ExerciseViewModel,
        resumeExerciseSession: () -> Unit,
        pauseExerciseSession: (Boolean) -> Unit,
        isAutoPauseEnabled: Boolean
    ): SensorEventListener? {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {

            return null
        }
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        var lastStepTimestamp = System.currentTimeMillis()

        val stepDetectorScope = CoroutineScope(Dispatchers.Main.immediate + Job())

        val stepDetectorListener = object : CleanupListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
                    lastStepTimestamp = System.currentTimeMillis()

                    val steps = event.values[0].toInt()
                    exerciseViewModel.stepCount.postValue(
                        (exerciseViewModel.stepCount.value ?: 0) + steps
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun cleanup() {
                stepDetectorScope.cancel()
            }
        }

        sensorManager.registerListener(
            stepDetectorListener,
            stepDetectorSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        // Monitorer l'inactivité pour l'auto-pause
        stepDetectorScope.launch {
            while (true) {
                delay(1000L)
                val currentTime = System.currentTimeMillis()
                val timeSinceLastStep = currentTime - lastStepTimestamp

                if (isAutoPauseEnabled &&
                    timeSinceLastStep > 5000 &&
                    exerciseViewModel.isSessionPaused.value == false &&
                    exerciseViewModel.isSessionActive.value == true
                ) {
                    pauseExerciseSession(true)
                } else if (isAutoPauseEnabled &&
                    timeSinceLastStep <= 5000 &&
                    exerciseViewModel.isSessionPaused.value == true &&
                    exerciseViewModel.isAutoPaused.value == true &&
                    exerciseViewModel.isSessionActive.value == true
                ) {
                    resumeExerciseSession()
                }
            }
        }

        return stepDetectorListener
    }

    fun unregisterStepDetector(
        context: Context,
        stepDetectorListener: SensorEventListener?
    ) {
        stepDetectorListener?.let {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager.unregisterListener(it)

            if (it is CleanupListener) {
                it.cleanup()
            }
        }
    }

    interface CleanupListener : SensorEventListener {
        fun cleanup()
    }
}

