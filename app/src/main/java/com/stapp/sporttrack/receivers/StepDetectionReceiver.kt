package com.stapp.sporttrack.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.stapp.sporttrack.data.models.SharedExerciseState
import com.stapp.sporttrack.workers.StepDetectionService

class StepDetectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_USER_PRESENT
                -> {
                if (!SharedExerciseState.isSessionActive && !StepDetectionService.isRunning) {
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, StepDetectionService::class.java)
                    )
                }
            }
        }
    }
}
