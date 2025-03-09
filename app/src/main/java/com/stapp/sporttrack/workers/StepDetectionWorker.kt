package com.stapp.sporttrack.workers

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stapp.sporttrack.data.models.SharedExerciseState

class StepDetectionWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        if (!SharedExerciseState.isSessionActive && !StepDetectionService.isRunning) {
            ContextCompat.startForegroundService(
                applicationContext,
                Intent(applicationContext, StepDetectionService::class.java)
            )
        }
        return Result.success()
    }
}