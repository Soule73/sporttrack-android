package com.stapp.sporttrack.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.SharedExerciseState
import com.stapp.sporttrack.ui.MainActivity
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.viewmodel.ExerciseViewModelHolder
import kotlinx.coroutines.*

class StepDetectionService : Service() {

    companion object {
        var isRunning = false
        private const val NOTIFICATION_ID = 2
        private const val CHANNEL_ID = "step_detection_channel"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null
    private var stepDetectorListener: SensorEventListener? = null
    private var lastStepTimestamp: Long = 0L
    private val inactivityThreshold = 5000L // 5 secondes d'inactivité

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        // On laisse le service démarrer même s'il n'y a pas de session active (auto mode)
        isRunning = true

        // À la première détection, on initialise sessionStartTime (s'il n'est pas déjà défini)
        if (SharedExerciseState.sessionStartTime <= 0L) {
            SharedExerciseState.sessionStartTime = 0L  // On attend la première détection pour l'initialiser
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        stepDetectorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
                    lastStepTimestamp = System.currentTimeMillis()
                    // Si aucune session n'est en cours, démarrez-en une nouvelle
                    if (SharedExerciseState.sessionStartTime == 0L) {
                        SharedExerciseState.sessionStartTime = System.currentTimeMillis()
                        SharedExerciseState.stepCount = 0
                        SharedExerciseState.elapsedTime = 0L
                        SharedExerciseState.totalDistance = 0.0
                        SharedExerciseState.stats = com.stapp.sporttrack.data.models.ExerciseStats()
                    }
                    // Incrémente le compteur de pas
                    SharedExerciseState.stepCount += event.values[0].toInt()
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            stepDetectorListener,
            stepDetectorSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        // Vérifier l'inactivité périodiquement
        coroutineScope.launch {
            while (isActive) {
                delay(1000L)
                // Si une session est en cours (sessionStartTime != 0), et qu'aucun pas n'a été détecté pendant le seuil...
                if (SharedExerciseState.sessionStartTime != 0L) {
                    val timeSinceLastStep = System.currentTimeMillis() - lastStepTimestamp
                    if (timeSinceLastStep > inactivityThreshold && SharedExerciseState.stepCount > 0) {
                        finalizeStepSession()
                    }
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Step Detection",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Détection de pas active")
            .setContentText("Le service de détection fonctionne en arrière-plan")
            .setSmallIcon(R.drawable.ic_directions_walk)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun finalizeStepSession() {
        // Fixer l'heure de fin de session
        SharedExerciseState.sessionEndTime = System.currentTimeMillis()
        // Calculer la durée réelle de la session
        SharedExerciseState.elapsedTime = SharedExerciseState.sessionEndTime - SharedExerciseState.sessionStartTime

        // Désinscrire temporairement le capteur (pour éviter des appels concurrentiels pendant la finalisation)
        sensorManager.unregisterListener(stepDetectorListener)

        // Calculer les statistiques via la WalkingStrategy
        val walkingStrategy = ExerciseUtils.getExerciseStrategy(ExerciseSessionRecord.EXERCISE_TYPE_WALKING)
        // Utiliser le poids de l'utilisateur s'il est défini (> 0), sinon 70.0 kg par défaut
        val weight = if (SharedExerciseState.userWeight!! > 0.0) SharedExerciseState.userWeight else 70.0
        val calculatedStats = walkingStrategy.updateExerciseStats(
            weightInKg = weight,
            elapsedTime = SharedExerciseState.elapsedTime,
            totalDistance = SharedExerciseState.totalDistance,
            stepCount = SharedExerciseState.stepCount,
            lastLocation = SharedExerciseState.lastLocation,
            newLocation = null
        )
        SharedExerciseState.stats = calculatedStats

        // Envoyer les données via le ViewModel
        ExerciseViewModelHolder.instance?.sendStepSessionData(isAuto = true) {
            // Callback optionnel après l'envoi
        }


        // Réinitialiser les champs de session pour démarrer une nouvelle session lors de la prochaine détection
        SharedExerciseState.sessionStartTime = 0L
        SharedExerciseState.sessionEndTime = 0L
        SharedExerciseState.elapsedTime = 0L
        SharedExerciseState.stepCount = 0
        SharedExerciseState.totalDistance = 0.0
        SharedExerciseState.stats = com.stapp.sporttrack.data.models.ExerciseStats()

        // Réenregistrer le listener afin que le service continue à détecter les pas
        sensorManager.registerListener(
            stepDetectorListener,
            stepDetectorSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(stepDetectorListener)
        coroutineScope.cancel()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null
}


//class StepDetectionService : Service() {
//
//    companion object {
//        var isRunning = false
//        private const val NOTIFICATION_ID = 2
//        private const val CHANNEL_ID = "step_detection_channel"
//    }
//
//    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
//    private lateinit var sensorManager: SensorManager
//    private var stepDetectorSensor: Sensor? = null
//    private var stepDetectorListener: SensorEventListener? = null
//    private var lastStepTimestamp: Long = 0L
//    private val inactivityThreshold = 5000L
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//
//        startForeground(NOTIFICATION_ID, createNotification())
//
//        if (SharedExerciseState.isSessionActive) {
//            stopSelf()
//            return
//        }
//        isRunning = true
//
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
//
//        stepDetectorListener = object : SensorEventListener {
//            override fun onSensorChanged(event: SensorEvent?) {
//                if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
//                    lastStepTimestamp = System.currentTimeMillis()
//
//                    SharedExerciseState.stepCount += event.values[0].toInt()
//                }
//            }
//
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
//        }
//
//        sensorManager.registerListener(
//            stepDetectorListener,
//            stepDetectorSensor,
//            SensorManager.SENSOR_DELAY_UI
//        )
//
//        coroutineScope.launch {
//            while (isActive) {
//                delay(1000L)
//
//                if (SharedExerciseState.isSessionActive) {
//                    stopSelf()
//                    break
//                }
//                val timeSinceLastStep = System.currentTimeMillis() - lastStepTimestamp
//                if (timeSinceLastStep > inactivityThreshold && SharedExerciseState.stepCount > 0) {
//                    finalizeStepSession()
//                    break
//                }
//            }
//        }
//    }
//
//    private fun createNotificationChannel() {
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            "Step Detection",
//            NotificationManager.IMPORTANCE_LOW
//        )
//        val notificationManager = getSystemService(NotificationManager::class.java)
//        notificationManager.createNotificationChannel(channel)
//    }
//
//    private fun createNotification(): Notification {
//        val notificationIntent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        return NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Détection de pas active")
//            .setContentText("Le service de détection fonctionne en arrière-plan")
//            .setSmallIcon(R.drawable.ic_directions_walk)
//            .setContentIntent(pendingIntent)
//            .build()
//    }
//
//    private fun finalizeStepSession() {
//        if (ExerciseViewModelHolder.instance == null) return
//        SharedExerciseState.sessionEndTime = System.currentTimeMillis()
//
//        sensorManager.unregisterListener(stepDetectorListener)
//        val walkingStrategy =
//            ExerciseUtils.getExerciseStrategy(ExerciseSessionRecord.EXERCISE_TYPE_WALKING)
//        val calculatedStats = walkingStrategy.updateExerciseStats(
//            weightInKg = SharedExerciseState.userWeight,
//            elapsedTime = SharedExerciseState.elapsedTime,
//            totalDistance = SharedExerciseState.totalDistance,
//            stepCount = SharedExerciseState.stepCount,
//            lastLocation = SharedExerciseState.lastLocation,
//            newLocation = null
//        )
//        SharedExerciseState.stats = calculatedStats
//
//        ExerciseViewModelHolder.instance?.sendStepSessionData(isAuto = true) {
//        }
//        stopSelf()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        sensorManager.unregisterListener(stepDetectorListener)
//        coroutineScope.cancel()
//        isRunning = false
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}
