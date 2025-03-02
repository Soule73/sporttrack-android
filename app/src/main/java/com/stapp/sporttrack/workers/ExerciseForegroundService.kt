package com.stapp.sporttrack.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.SharedExerciseState
import com.stapp.sporttrack.ui.MainActivity
import com.stapp.sporttrack.ui.navigation.EXERCISE_TYPE_NAV_ARGUMENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class ExerciseForegroundService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private val notificationId = 1
    private val channelId = "exercise_channel"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(notificationId, createNotification())

        coroutineScope.launch {
            while (SharedExerciseState.isSessionActive) {

                updateNotification()
                delay(1000L)
            }

            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXERCISE_TYPE_NAV_ARGUMENT, SharedExerciseState.exerciseType)

        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = getContentText()

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Session d'exercice en cours")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val contentText = getContentText()
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXERCISE_TYPE_NAV_ARGUMENT, SharedExerciseState.exerciseType)

        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Session d'exercice en cours")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun getContentText(): String {
        val formattedTime = formatElapsedTime(SharedExerciseState.elapsedTime)
        return when (SharedExerciseState.exerciseType) {
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Durée : $formattedTime, Pas : ${SharedExerciseState.stepCount}"
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Durée : $formattedTime, Distance : ${SharedExerciseState.stats.totalDistance} km"
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Durée : $formattedTime, Vitesse : ${SharedExerciseState.stats.averageSpeed} km/h"
            else -> "Durée : $formattedTime"
        }
    }

    private fun formatElapsedTime(elapsedTime: Long): String {
        val totalSeconds = elapsedTime / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    private fun createNotificationChannel() {
        val channelName = "Session d'exercice"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
