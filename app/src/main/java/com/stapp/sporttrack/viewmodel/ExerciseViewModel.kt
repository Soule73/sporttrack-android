package com.stapp.sporttrack.viewmodel

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.stapp.sporttrack.data.models.AddExerciseSessionRequest
import com.stapp.sporttrack.data.models.DailyExerciseDetailsResponse
import com.stapp.sporttrack.data.models.DailyExerciseStatsResponse
import com.stapp.sporttrack.data.models.ExerciseSessionResponse
import com.stapp.sporttrack.data.models.WeeklyExerciseStatsResponse
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.ExerciseStatsUnit
import com.stapp.sporttrack.data.repository.ExerciseRepository
import com.stapp.sporttrack.workers.ExerciseForegroundService
import com.stapp.sporttrack.data.models.SharedExerciseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random

fun toSeconds(formattedTime: String): Int {
    val parts = formattedTime.split(":")
    val minutes = parts[0].toInt()
    val seconds = parts[1].toInt()
    return minutes * 60 + seconds
}

fun toFormattedTime(seconds: Int): String {
    val formattedMinutes = seconds / 60
    val formattedSeconds = seconds % 60

    var minAndSeconds=""

    if(formattedMinutes > 0){
        minAndSeconds = "$formattedMinutes "+ ExerciseStatsUnit.MIN
    }
    if(formattedSeconds > 0){
        minAndSeconds += " $formattedSeconds "+ ExerciseStatsUnit.SEC
    }

    return minAndSeconds
}


class ExerciseViewModelFactory(
    private val exerciseRepository: ExerciseRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {

    private val userId = MutableLiveData(0)
    val isSessionActive = MutableLiveData(false)
    val isSessionPaused = MutableLiveData(false)
    val isAutoPaused = MutableLiveData(false)
    val elapsedTime = MutableLiveData(0L)
    val totalDistance = MutableLiveData(0.0)
    val stepCount = MutableLiveData(0)
    val lastLocation = MutableLiveData<LatLng?>()
    val stats = MutableLiveData(ExerciseStats())
    private val exerciseType = MutableLiveData<Int>()
    val startTime = MutableLiveData<Long>()
    private val exerciseStartTime = MutableLiveData<Long?>()
    private val endTime = MutableLiveData<Long?>()

    private val _addExerciseResult = MutableLiveData<Result<Int>?>()
    val addExerciseResult: LiveData<Result<Int>?> = _addExerciseResult

    private val _dailyExerciseStats = MutableStateFlow<List<DailyExerciseStatsResponse>>(emptyList())
    val dailyExerciseStats: StateFlow<List<DailyExerciseStatsResponse>> = _dailyExerciseStats
    private var statsLoaded = false

    private val _dailyExerciseDetails = MutableStateFlow<DailyExerciseDetailsResponse?>(null)
    val dailyExerciseDetails: StateFlow<DailyExerciseDetailsResponse?> = _dailyExerciseDetails

    private val _weeklyExerciseStats = MutableStateFlow<WeeklyExerciseStatsResponse?>(null)
    val weeklyExerciseStats: StateFlow<WeeklyExerciseStatsResponse?> = _weeklyExerciseStats
    private var weeklyStatsLoaded = false

    private val _exerciseSessions = MutableStateFlow<Map<LocalDate, List<ExerciseSessionResponse>>>(emptyMap())
    val exerciseSessions: StateFlow<Map<LocalDate, List<ExerciseSessionResponse>>> = _exerciseSessions

    private val _exerciseSession = MutableStateFlow<ExerciseSessionResponse?>(null)
    val exerciseSession: StateFlow<ExerciseSessionResponse?> = _exerciseSession

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

//    private val _error = MutableStateFlow<String?>(null)
//    val error: StateFlow<String?> = _error

    fun initializeFromSharedState() {
        isSessionActive.value = SharedExerciseState.isSessionActive
        isSessionPaused.value = SharedExerciseState.isSessionPaused
        isAutoPaused.value = SharedExerciseState.isAutoPaused
        elapsedTime.value = SharedExerciseState.elapsedTime
        totalDistance.value = SharedExerciseState.totalDistance
        stepCount.value = SharedExerciseState.stepCount
        lastLocation.value = SharedExerciseState.lastLocation
        stats.value = SharedExerciseState.stats
        exerciseType.value = SharedExerciseState.exerciseType
        startTime.value = SharedExerciseState.startTime
        exerciseStartTime.value = SharedExerciseState.sessionStartTime
        endTime.value = SharedExerciseState.sessionEndTime
        userId.value = SharedExerciseState.userId

        setupObservers()
    }

    private fun setupObservers() {

        isSessionActive.observeForever { updateSharedState() }
        isSessionPaused.observeForever { updateSharedState() }
        isAutoPaused.observeForever { updateSharedState() }
        elapsedTime.observeForever { updateSharedState() }
        totalDistance.observeForever { updateSharedState() }
        stepCount.observeForever { updateSharedState() }
        lastLocation.observeForever { updateSharedState() }
        stats.observeForever { updateSharedState() }
        exerciseType.observeForever { updateSharedState() }

        startTime.observeForever { updateSharedState() }
        exerciseStartTime.observeForever { updateSharedState() }
        endTime.observeForever { updateSharedState() }
    }

    private fun updateSharedState() {
        if (isSessionPaused.value == true) return

        SharedExerciseState.isSessionActive = isSessionActive.value == true
        SharedExerciseState.isSessionPaused = isSessionPaused.value == true
        SharedExerciseState.isAutoPaused = isAutoPaused.value == true
        SharedExerciseState.elapsedTime = elapsedTime.value ?: 0L
        SharedExerciseState.totalDistance = totalDistance.value ?: 0.0
        SharedExerciseState.stepCount = stepCount.value ?: 0
        SharedExerciseState.lastLocation = lastLocation.value
        SharedExerciseState.stats = stats.value ?: ExerciseStats()
        SharedExerciseState.exerciseType =
            exerciseType.value ?: ExerciseSessionRecord.EXERCISE_TYPE_WALKING

        SharedExerciseState.startTime = startTime.value ?: 0L
        SharedExerciseState.sessionStartTime = exerciseStartTime.value ?: 0L
        SharedExerciseState.sessionEndTime = endTime.value ?: 0L
    }

    override fun onCleared() {
        super.onCleared()
        isSessionActive.removeObserver { updateSharedState() }
        isSessionPaused.removeObserver { updateSharedState() }
        isAutoPaused.removeObserver { updateSharedState() }
        elapsedTime.removeObserver { updateSharedState() }
        totalDistance.removeObserver { updateSharedState() }
        stepCount.removeObserver { updateSharedState() }
        lastLocation.removeObserver { updateSharedState() }
        stats.removeObserver { updateSharedState() }
        exerciseType.removeObserver { updateSharedState() }

        startTime.removeObserver { updateSharedState() }
        exerciseStartTime.removeObserver { updateSharedState() }
        endTime.removeObserver { updateSharedState() }
    }

    fun setExerciseType(exerciseType: Int) {
        this.exerciseType.value = exerciseType
        SharedExerciseState.exerciseType = exerciseType
    }

    fun startExerciseSession(context: Context, userId: Int? = null) {
        if (isSessionActive.value == true) return

        SharedExerciseState.userId = userId

        isSessionActive.value = true
        isSessionPaused.value = false
        isAutoPaused.value = false
        startTime.value = SystemClock.elapsedRealtime() - (elapsedTime.value ?: 0L)

        exerciseStartTime.value = null
        exerciseStartTime.value = System.currentTimeMillis()

        endTime.value = null
        // Démarrer le service
        val intent = Intent(context, ExerciseForegroundService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun pauseExerciseSession(autoPause: Boolean = false) {
        isSessionPaused.value = true
        isAutoPaused.value = autoPause

        // Mettre à jour l'état partagé pour refléter la pause
        updateSharedState()
    }

    fun resumeExerciseSession() {
        if (isSessionPaused.value != true) return

        isSessionPaused.value = false
        isAutoPaused.value = false
        startTime.value = SystemClock.elapsedRealtime() - (elapsedTime.value ?: 0L)

        // Mettre à jour l'état partagé pour refléter la reprise
        updateSharedState()
    }

    fun stopExerciseSession(context: Context) {
        isSessionActive.value = false
        isSessionPaused.value = false
        isAutoPaused.value = false

        endTime.value = System.currentTimeMillis()
        // Réinitialiser les variables d'état
//        resetState()
        addExerciseSession()
        // Arrêter le service
        val intent = Intent(context, ExerciseForegroundService::class.java)
        context.stopService(intent)
    }

     private fun resetState() {
        elapsedTime.value = 0L
        totalDistance.value = 0.0
        stepCount.value = 0
        lastLocation.value = null
        stats.value = ExerciseStats()

        SharedExerciseState.reset()
    }

    private fun addExerciseSession() {
        _isLoading.value = true
        val userId = userId.value ?: SharedExerciseState.userId
        if (userId != null) {

            viewModelScope.launch {
                val activityTypeId =
                    exerciseType.value ?: ExerciseSessionRecord.EXERCISE_TYPE_WALKING

                val startDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(exerciseStartTime.value ?: 0L),
                    ZoneId.systemDefault()
                )
                val endDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(endTime.value ?: System.currentTimeMillis()),
                    ZoneId.systemDefault()
                )

                val durationInSeconds = stats.value?.totalDuration?.let { toSeconds(it) } ?: 0

               val exerciseSessionResponse = ExerciseSessionResponse(
                    userId = userId,
                    activityTypeId = activityTypeId,
                    startDate = startDateTime,
                    endDate = endDateTime,
                    duration = durationInSeconds,
                    distance = stats.value?.totalDistance,
                    caloriesBurned = stats.value?.calories,
                    averageSpeed = stats.value?.averageSpeed,
                    stepCount = stats.value?.stepCount,
                    cadence = stats.value?.cadence,
                    rhythm = stats.value?.rhythm,
                    slope = stats.value?.slope,
                    comment = null,
                    status = "private",
                    sessionId = Random.nextInt(0, 60)
                )

                _exerciseSession.value = exerciseSessionResponse
                val exerciseRequest = AddExerciseSessionRequest(
                    userId = userId,
                    activityTypeId = activityTypeId,
                    startDate = startDateTime,
                    endDate = endDateTime,
                    duration = durationInSeconds,
                    distance = stats.value?.totalDistance,
                    caloriesBurned = stats.value?.calories,
                    averageSpeed = stats.value?.averageSpeed,
                    stepCount = stats.value?.stepCount,
                    cadence = stats.value?.cadence,
                    rhythm = stats.value?.rhythm,
                    slope = stats.value?.slope,
                    comment = null,
                    status = "private"
                )

            val result = exerciseRepository.addExerciseSession(exerciseRequest)
            _addExerciseResult.postValue(result)

                // Réinitialiser l'état après l'envoi
//                resetState()
            }
        }
        _isLoading.value = false
    }

    fun loadDailyExerciseStats() {
//        if(isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            val result = exerciseRepository.getDailyExerciseStats()
            if (result.isSuccess) {

                _dailyExerciseStats.value = result.getOrDefault(emptyList())
            }
        }
        _isLoading.value = false
    }

    fun loadDailyExerciseDetails(date: LocalDate) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getDailyExerciseDetails(date)
            if (result.isSuccess) {
                _dailyExerciseDetails.value = result.getOrNull()
            }
        }
        _isLoading.value = false
    }

    fun loadWeeklyExerciseStats() {
//        if(isLoading.value) return
//        if (weeklyStatsLoaded) return
        _isLoading.value=true
        viewModelScope.launch {
            val result = exerciseRepository.getWeeklyExerciseStats()
            if (result.isSuccess) {
                _weeklyExerciseStats.value = result.getOrNull()
//                weeklyStatsLoaded = true
            }
        }
        _isLoading.value = false
    }

    fun loadExerciseSessions() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getExerciseSessionsByUser()
            if (result.isSuccess) {
                _exerciseSessions.value = result.getOrDefault(emptyList()).groupBy { it.startDate.toLocalDate() }
            }
        }
        _isLoading.value = false
    }

    fun loadExerciseSessionById(sessionId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getExerciseSessionById(sessionId)
            if (result.isSuccess) {
                _exerciseSession.value = result.getOrNull()
            }
        }
        _isLoading.value = false
    }

    fun resetAddExerciseResult() {
        resetState()
        _addExerciseResult.value = null
    }

    fun setSelectedSession(session: ExerciseSessionResponse) {
        _exerciseSession.value=session
    }
}
