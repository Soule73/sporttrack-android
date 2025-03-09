package com.stapp.sporttrack.viewmodel

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stapp.sporttrack.data.models.AddExerciseSessionRequest
import com.stapp.sporttrack.data.models.DailyExerciseDetailsResponse
import com.stapp.sporttrack.data.models.DailyExerciseStatsResponse
import com.stapp.sporttrack.data.models.ExerciseSessionResponse
import com.stapp.sporttrack.data.models.SharedExerciseState
import com.stapp.sporttrack.data.models.WeeklyExerciseStatsResponse
import com.stapp.sporttrack.data.repository.CustomException
import com.stapp.sporttrack.data.repository.ExerciseRepository
import com.stapp.sporttrack.utils.toSeconds
import com.stapp.sporttrack.workers.ExerciseForegroundService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.random.Random

class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {

    // Le résultat de l'ajout de session (backend) est conservé via LiveData
    private val _addExerciseResult = MutableLiveData<Result<Int>?>()
    val addExerciseResult: LiveData<Result<Int>?> = _addExerciseResult

    // StateFlows pour la gestion des données mises en cache (sessions, stats journalières/hebdomadaires)
    private val _dailyExerciseStats =
        MutableStateFlow<List<DailyExerciseStatsResponse>>(emptyList())
    val dailyExerciseStats: StateFlow<List<DailyExerciseStatsResponse>> = _dailyExerciseStats

    private val _dailyExerciseDetails = MutableStateFlow<DailyExerciseDetailsResponse?>(null)
    val dailyExerciseDetails: StateFlow<DailyExerciseDetailsResponse?> = _dailyExerciseDetails

    private val _weeklyExerciseStats = MutableStateFlow<WeeklyExerciseStatsResponse?>(null)
    val weeklyExerciseStats: StateFlow<WeeklyExerciseStatsResponse?> = _weeklyExerciseStats

    private val _exerciseSessions =
        MutableStateFlow<Map<LocalDate, List<ExerciseSessionResponse>>>(emptyMap())
    val exerciseSessions: StateFlow<Map<LocalDate, List<ExerciseSessionResponse>>> =
        _exerciseSessions

    private val _exerciseSession = MutableStateFlow<ExerciseSessionResponse?>(null)
    val exerciseSession: StateFlow<ExerciseSessionResponse?> = _exerciseSession

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Pour le caching
    private var exerciseSessionsLastFetched: Long = 0L
    private var dailyStatsLastFetched: Long = 0L
    private var weeklyStatsLastFetched: Long = 0L
    private val cacheDuration = 5 * 60 * 1000L

    fun setExerciseType(type: Int) {
        SharedExerciseState.exerciseType = type
    }

    fun startExerciseSession(context: Context, userId: Int? = null) {
        if (SharedExerciseState.isSessionActive) return

        SharedExerciseState.userId = userId
        SharedExerciseState.isSessionActive = true
        SharedExerciseState.isSessionPaused = false
        SharedExerciseState.isAutoPaused = false
        SharedExerciseState.startTime =
            SystemClock.elapsedRealtime() - SharedExerciseState.elapsedTime
        SharedExerciseState.sessionStartTime = System.currentTimeMillis()
        SharedExerciseState.sessionEndTime = 0L

        // Démarrage du service en premier plan
        val intent = Intent(context, ExerciseForegroundService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun pauseExerciseSession(autoPause: Boolean = false) {
        SharedExerciseState.isSessionPaused = true
        SharedExerciseState.isAutoPaused = autoPause
    }

    fun resumeExerciseSession() {
        if (!SharedExerciseState.isSessionPaused) return

        SharedExerciseState.isSessionPaused = false
        SharedExerciseState.isAutoPaused = false
        SharedExerciseState.startTime =
            SystemClock.elapsedRealtime() - SharedExerciseState.elapsedTime
    }

    fun stopExerciseSession(context: Context) {
        if (!SharedExerciseState.isSessionActive) return

        SharedExerciseState.isSessionActive = false
        SharedExerciseState.isSessionPaused = false
        SharedExerciseState.isAutoPaused = false
        SharedExerciseState.sessionEndTime = System.currentTimeMillis()

        addExerciseSession {
            refreshDataAfterExerciseCompletion()
        }

        val intent = Intent(context, ExerciseForegroundService::class.java)
        context.stopService(intent)
    }

    private fun resetState() {
        SharedExerciseState.reset()
    }

    private fun createExerciseRequest(
        userId: Int,
        activityTypeId: Int,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        durationInSeconds: Int,
        isAuto: Boolean = false
    ): AddExerciseSessionRequest {
        return AddExerciseSessionRequest(
            userId = userId,
            activityTypeId = activityTypeId,
            startDate = startDateTime,
            endDate = endDateTime,
            duration = durationInSeconds,
            distance = SharedExerciseState.stats.totalDistance,
            caloriesBurned = SharedExerciseState.stats.calories,
            averageSpeed = SharedExerciseState.stats.averageSpeed,
            stepCount = SharedExerciseState.stats.stepCount,
            cadence = SharedExerciseState.stats.cadence,
            rhythm = SharedExerciseState.stats.rhythm,
            slope = SharedExerciseState.stats.slope,
            comment = null,
            status = "private",
            isAuto = isAuto
        )
    }

    private fun addExerciseSession(isAuto: Boolean = false, onCompletion: () -> Unit) {
        val userId = SharedExerciseState.userId
        if (userId != null) {
            viewModelScope.launch {
                val activityTypeId = SharedExerciseState.exerciseType
                val startDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(SharedExerciseState.sessionStartTime),
                    ZoneId.systemDefault()
                )
                val endDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(if (SharedExerciseState.sessionEndTime != 0L) SharedExerciseState.sessionEndTime else System.currentTimeMillis()),
                    ZoneId.systemDefault()
                )
                val durationInSeconds =
                    SharedExerciseState.stats.totalDuration.let { toSeconds(it) }
                val exerciseRequest = createExerciseRequest(
                    userId,
                    activityTypeId,
                    startDateTime,
                    endDateTime,
                    durationInSeconds,
                    isAuto
                )
                _exerciseSession.value = exerciseRequest.toExerciseSessionResponse()
                val result = exerciseRepository.addExerciseSession(exerciseRequest)
                _addExerciseResult.postValue(result)

                if (result.isSuccess) {
                    onCompletion()
                }

                resetState()
                _isLoading.value = false

            }
            _isLoading.value = true
        } else {
            _isLoading.value = false
        }
    }

    private fun AddExerciseSessionRequest.toExerciseSessionResponse(): ExerciseSessionResponse {
        return ExerciseSessionResponse(
            userId = this.userId,
            activityTypeId = this.activityTypeId,
            startDate = this.startDate,
            endDate = this.endDate,
            duration = this.duration,
            distance = this.distance,
            caloriesBurned = this.caloriesBurned,
            averageSpeed = this.averageSpeed,
            stepCount = this.stepCount,
            cadence = this.cadence,
            rhythm = this.rhythm,
            slope = this.slope,
            comment = this.comment,
            status = this.status,
            isAuto = this.isAuto,
            sessionId = Random.nextInt(0, 1000000)
        )

    }

    private fun shouldRefreshData(lastFetched: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastFetched) > cacheDuration
    }

    fun loadExerciseSessions(forceRefresh: Boolean = false) {
        if (!forceRefresh && _exerciseSessions.value.isNotEmpty() && !shouldRefreshData(
                exerciseSessionsLastFetched
            )
        ) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getExerciseSessionsByUser()
            if (result.isSuccess) {
                _exerciseSessions.value =
                    result.getOrDefault(emptyList()).groupBy { it.startDate.toLocalDate() }
                exerciseSessionsLastFetched = System.currentTimeMillis()
            } else {
                if (result.exceptionOrNull() != null && result.exceptionOrNull() is CustomException) {

                    println("error loading exercise sessions ${(result.exceptionOrNull() as CustomException).errorResponse}")
                }
            }
            _isLoading.value = false
        }
    }

    fun loadDailyExerciseStats(forceRefresh: Boolean = false) {
        if (!forceRefresh && _dailyExerciseStats.value.isNotEmpty() && !shouldRefreshData(
                dailyStatsLastFetched
            )
        ) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getDailyExerciseStats()
            if (result.isSuccess) {
                _dailyExerciseStats.value = result.getOrDefault(emptyList())
                dailyStatsLastFetched = System.currentTimeMillis()
            }
            _isLoading.value = false
        }
    }

    fun loadWeeklyExerciseStats(forceRefresh: Boolean = false) {
        if (!forceRefresh && _weeklyExerciseStats.value != null && !shouldRefreshData(
                weeklyStatsLastFetched
            )
        ) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getWeeklyExerciseStats()
            if (result.isSuccess) {
                _weeklyExerciseStats.value = result.getOrNull()
                weeklyStatsLastFetched = System.currentTimeMillis()
            }
            _isLoading.value = false
        }
    }

    fun loadDailyExerciseDetails(date: LocalDate, forceRefresh: Boolean = false) {
        if (!forceRefresh && _dailyExerciseDetails.value?.sessions?.get(0)?.startDate?.toLocalDate() == date) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getDailyExerciseDetails(date)
            if (result.isSuccess) {
                _dailyExerciseDetails.value = result.getOrNull()
            }
            _isLoading.value = false
        }
    }

    fun loadExerciseSessionById(sessionId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && _exerciseSession.value?.sessionId == sessionId) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            val result = exerciseRepository.getExerciseSessionById(sessionId)
            if (result.isSuccess) {
                _exerciseSession.value = result.getOrNull()
            }
            _isLoading.value = false
        }
    }

    private fun refreshDataAfterExerciseCompletion() {
        loadExerciseSessions(forceRefresh = true)
        loadDailyExerciseStats(forceRefresh = true)
        loadWeeklyExerciseStats(forceRefresh = true)
    }

    fun sendStepSessionData(isAuto: Boolean = false, onCompletion: () -> Unit) {
        addExerciseSession(isAuto, {
            onCompletion()
            refreshDataAfterExerciseCompletion()
            resetAddExerciseResult()
        })
    }

    fun resetAddExerciseResult() {
        resetState()
        _addExerciseResult.value = null
    }

    fun setSelectedSession(session: ExerciseSessionResponse) {
        _exerciseSession.value = session
    }
}

object ExerciseViewModelHolder {
    var instance: ExerciseViewModel? = null
}

//class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {
//
//    private val userId = MutableLiveData(0)
//    private val exerciseType = MutableLiveData<Int>()
//    val isSessionActive = MutableLiveData(false)
//    val isSessionPaused = MutableLiveData(false)
//    val isAutoPaused = MutableLiveData(false)
//    val elapsedTime = MutableLiveData(0L)
//    val totalDistance = MutableLiveData(0.0)
//    val stepCount = MutableLiveData(0)
//    val lastLocation = MutableLiveData<LatLng?>()
//    val startTime = MutableLiveData<Long>()
//    private val exerciseStartTime = MutableLiveData<Long?>()
//    private val endTime = MutableLiveData<Long?>()
//    val stats = MutableLiveData(ExerciseStats())
//
//    private val _addExerciseResult = MutableLiveData<Result<Int>?>()
//    val addExerciseResult: LiveData<Result<Int>?> = _addExerciseResult
//
//    private val _dailyExerciseStats =
//        MutableStateFlow<List<DailyExerciseStatsResponse>>(emptyList())
//    val dailyExerciseStats: StateFlow<List<DailyExerciseStatsResponse>> = _dailyExerciseStats
//
//    private val _dailyExerciseDetails = MutableStateFlow<DailyExerciseDetailsResponse?>(null)
//    val dailyExerciseDetails: StateFlow<DailyExerciseDetailsResponse?> = _dailyExerciseDetails
//
//    private val _weeklyExerciseStats = MutableStateFlow<WeeklyExerciseStatsResponse?>(null)
//    val weeklyExerciseStats: StateFlow<WeeklyExerciseStatsResponse?> = _weeklyExerciseStats
//
//    private val _exerciseSessions =
//        MutableStateFlow<Map<LocalDate, List<ExerciseSessionResponse>>>(emptyMap())
//    val exerciseSessions: StateFlow<Map<LocalDate, List<ExerciseSessionResponse>>> =
//        _exerciseSessions
//
//    private val _exerciseSession = MutableStateFlow<ExerciseSessionResponse?>(null)
//    val exerciseSession: StateFlow<ExerciseSessionResponse?> = _exerciseSession
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private var exerciseSessionsLastFetched: Long = 0L
//    private var dailyStatsLastFetched: Long = 0L
//    private var weeklyStatsLastFetched: Long = 0L
//
//    private val cacheDuration = 5 * 60 * 1000L
//
//    fun initializeFromSharedState() {
//        isSessionActive.value = SharedExerciseState.isSessionActive
//        isSessionPaused.value = SharedExerciseState.isSessionPaused
//        isAutoPaused.value = SharedExerciseState.isAutoPaused
//        elapsedTime.value = SharedExerciseState.elapsedTime
//        totalDistance.value = SharedExerciseState.totalDistance
//        stepCount.value = SharedExerciseState.stepCount
//        lastLocation.value = SharedExerciseState.lastLocation
//        stats.value = SharedExerciseState.stats
//        exerciseType.value = SharedExerciseState.exerciseType
//        startTime.value = SharedExerciseState.startTime
//        exerciseStartTime.value = SharedExerciseState.sessionStartTime
//        endTime.value = SharedExerciseState.sessionEndTime
//        userId.value = SharedExerciseState.userId
//
//        setupObservers()
//    }
//
//    private fun setupObservers() {
//        isSessionActive.observeForever { updateSharedState() }
//        isSessionPaused.observeForever { updateSharedState() }
//        isAutoPaused.observeForever { updateSharedState() }
//        elapsedTime.observeForever { updateSharedState() }
//        totalDistance.observeForever { updateSharedState() }
//        stepCount.observeForever { updateSharedState() }
//        lastLocation.observeForever { updateSharedState() }
//        stats.observeForever { updateSharedState() }
//        exerciseType.observeForever { updateSharedState() }
//        startTime.observeForever { updateSharedState() }
//        exerciseStartTime.observeForever { updateSharedState() }
//        endTime.observeForever { updateSharedState() }
//    }
//
//    private fun updateSharedState() {
//        if (isSessionPaused.value == true) return
//
//        SharedExerciseState.isSessionActive = isSessionActive.value == true
//        SharedExerciseState.isSessionPaused = isSessionPaused.value == true
//        SharedExerciseState.isAutoPaused = isAutoPaused.value == true
//        SharedExerciseState.elapsedTime = elapsedTime.value ?: 0L
//        SharedExerciseState.totalDistance = totalDistance.value ?: 0.0
//        SharedExerciseState.stepCount = stepCount.value ?: 0
//        SharedExerciseState.lastLocation = lastLocation.value
//        SharedExerciseState.stats = stats.value ?: ExerciseStats()
//        SharedExerciseState.exerciseType =
//            exerciseType.value ?: ExerciseSessionRecord.EXERCISE_TYPE_WALKING
//        SharedExerciseState.startTime = startTime.value ?: 0L
//        SharedExerciseState.sessionStartTime = exerciseStartTime.value ?: 0L
//        SharedExerciseState.sessionEndTime = endTime.value ?: 0L
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        isSessionActive.removeObserver { updateSharedState() }
//        isSessionPaused.removeObserver { updateSharedState() }
//        isAutoPaused.removeObserver { updateSharedState() }
//        elapsedTime.removeObserver { updateSharedState() }
//        totalDistance.removeObserver { updateSharedState() }
//        stepCount.removeObserver { updateSharedState() }
//        lastLocation.removeObserver { updateSharedState() }
//        stats.removeObserver { updateSharedState() }
//        exerciseType.removeObserver { updateSharedState() }
//        startTime.removeObserver { updateSharedState() }
//        exerciseStartTime.removeObserver { updateSharedState() }
//        endTime.removeObserver { updateSharedState() }
//    }
//
//    fun setExerciseType(exerciseType: Int) {
//        this.exerciseType.value = exerciseType
//        SharedExerciseState.exerciseType = exerciseType
//    }
//
//    fun startExerciseSession(context: Context, userId: Int? = null) {
//        if (isSessionActive.value == true) return
//
//        SharedExerciseState.userId = userId
//
//        isSessionActive.value = true
//        isSessionPaused.value = false
//        isAutoPaused.value = false
//        startTime.value = SystemClock.elapsedRealtime() - (elapsedTime.value ?: 0L)
//
//        exerciseStartTime.value = System.currentTimeMillis()
//        endTime.value = null
//
//        val intent = Intent(context, ExerciseForegroundService::class.java)
//        ContextCompat.startForegroundService(context, intent)
//    }
//
//    fun pauseExerciseSession(autoPause: Boolean = false) {
//        isSessionPaused.value = true
//        isAutoPaused.value = autoPause
//
//        updateSharedState()
//    }
//
//    fun resumeExerciseSession() {
//        if (isSessionPaused.value != true) return
//
//        isSessionPaused.value = false
//        isAutoPaused.value = false
//        startTime.value = SystemClock.elapsedRealtime() - (elapsedTime.value ?: 0L)
//
//        updateSharedState()
//    }
//
//    fun stopExerciseSession(context: Context) {
//        if (isSessionActive.value != true) return
//
//        isSessionActive.value = false
//        isSessionPaused.value = false
//        isAutoPaused.value = false
//
//        endTime.value = System.currentTimeMillis()
//
//        addExerciseSession {
//            refreshDataAfterExerciseCompletion()
//        }
//
//        val intent = Intent(context, ExerciseForegroundService::class.java)
//        context.stopService(intent)
//    }
//
//    private fun resetState() {
//        elapsedTime.value = 0L
//        totalDistance.value = 0.0
//        stepCount.value = 0
//        lastLocation.value = null
//        stats.value = ExerciseStats()
//        exerciseStartTime.value = null
//        endTime.value = null
//        userId.value = 0
//
//        SharedExerciseState.reset()
//    }
//
//    private fun addExerciseSession(onCompletion: () -> Unit) {
//        val userId = this.userId.value ?: SharedExerciseState.userId
//        if (userId != null) {
//            viewModelScope.launch {
//                val activityTypeId =
//                    exerciseType.value ?: ExerciseSessionRecord.EXERCISE_TYPE_WALKING
//
//                val startDateTime = LocalDateTime.ofInstant(
//                    Instant.ofEpochMilli(exerciseStartTime.value ?: 0L),
//                    ZoneId.systemDefault()
//                )
//                val endDateTime = LocalDateTime.ofInstant(
//                    Instant.ofEpochMilli(endTime.value ?: System.currentTimeMillis()),
//                    ZoneId.systemDefault()
//                )
//
//                val durationInSeconds = stats.value?.totalDuration?.let { toSeconds(it) } ?: 0
//
//                val exerciseSessionResponse = ExerciseSessionResponse(
//                    userId = userId,
//                    activityTypeId = activityTypeId,
//                    startDate = startDateTime,
//                    endDate = endDateTime,
//                    duration = durationInSeconds,
//                    distance = stats.value?.totalDistance,
//                    caloriesBurned = stats.value?.calories,
//                    averageSpeed = stats.value?.averageSpeed,
//                    stepCount = stats.value?.stepCount,
//                    cadence = stats.value?.cadence,
//                    rhythm = stats.value?.rhythm,
//                    slope = stats.value?.slope,
//                    comment = null,
//                    status = "private",
//                    sessionId = Random.nextInt(0, 1000000) // Generate a random session ID
//                )
//
//                _exerciseSession.value = exerciseSessionResponse
//
//                _isLoading.value = true
//
//                val exerciseRequest = AddExerciseSessionRequest(
//                    userId = userId,
//                    activityTypeId = activityTypeId,
//                    startDate = startDateTime,
//                    endDate = endDateTime,
//                    duration = durationInSeconds,
//                    distance = stats.value?.totalDistance,
//                    caloriesBurned = stats.value?.calories,
//                    averageSpeed = stats.value?.averageSpeed,
//                    stepCount = stats.value?.stepCount,
//                    cadence = stats.value?.cadence,
//                    rhythm = stats.value?.rhythm,
//                    slope = stats.value?.slope,
//                    comment = null,
//                    status = "private"
//                )
//
//                val result = exerciseRepository.addExerciseSession(exerciseRequest)
//                _addExerciseResult.postValue(result)
//
//                if (result.isSuccess) {
//                    onCompletion()
//                }
//
//                resetState()
//                _isLoading.value = false
//            }
//        } else {
//            _isLoading.value = false
//        }
//    }
//
//    private fun shouldRefreshData(lastFetched: Long): Boolean {
//        val currentTime = System.currentTimeMillis()
//        return (currentTime - lastFetched) > cacheDuration
//    }
//
//    fun loadExerciseSessions(forceRefresh: Boolean = false) {
//        if (!forceRefresh && _exerciseSessions.value.isNotEmpty() && !shouldRefreshData(
//                exerciseSessionsLastFetched
//            )
//        ) {
//            return
//        }
//        _isLoading.value = true
//        viewModelScope.launch {
//            val result = exerciseRepository.getExerciseSessionsByUser()
//            if (result.isSuccess) {
//                _exerciseSessions.value =
//                    result.getOrDefault(emptyList()).groupBy { it.startDate.toLocalDate() }
//                exerciseSessionsLastFetched = System.currentTimeMillis()
//            }
//            _isLoading.value = false
//        }
//    }
//
//    fun loadDailyExerciseStats(forceRefresh: Boolean = false) {
//        if (!forceRefresh && _dailyExerciseStats.value.isNotEmpty() && !shouldRefreshData(
//                dailyStatsLastFetched
//            )
//        ) {
//            return
//        }
//        _isLoading.value = true
//        viewModelScope.launch {
//            val result = exerciseRepository.getDailyExerciseStats()
//            if (result.isSuccess) {
//                _dailyExerciseStats.value = result.getOrDefault(emptyList())
//                dailyStatsLastFetched = System.currentTimeMillis()
//            }
//            _isLoading.value = false
//        }
//    }
//
//    fun loadWeeklyExerciseStats(forceRefresh: Boolean = false) {
//        if (!forceRefresh && _weeklyExerciseStats.value != null && !shouldRefreshData(
//                weeklyStatsLastFetched
//            )
//        ) {
//            return
//        }
//        _isLoading.value = true
//        viewModelScope.launch {
//            val result = exerciseRepository.getWeeklyExerciseStats()
//            if (result.isSuccess) {
//                _weeklyExerciseStats.value = result.getOrNull()
//                weeklyStatsLastFetched = System.currentTimeMillis()
//            }
//            _isLoading.value = false
//        }
//    }
//
//    fun loadDailyExerciseDetails(date: LocalDate, forceRefresh: Boolean = false) {
//        if (!forceRefresh && _dailyExerciseDetails.value?.sessions?.get(0)?.startDate?.toLocalDate() == date) {
//            return
//        }
//        _isLoading.value = true
//        viewModelScope.launch {
//            val result = exerciseRepository.getDailyExerciseDetails(date)
//            if (result.isSuccess) {
//                _dailyExerciseDetails.value = result.getOrNull()
//            }
//            _isLoading.value = false
//        }
//    }
//
//    fun loadExerciseSessionById(sessionId: Int, forceRefresh: Boolean = false) {
//        if (!forceRefresh && _exerciseSession.value?.sessionId == sessionId) {
//            return
//        }
//        _isLoading.value = true
//        viewModelScope.launch {
//            val result = exerciseRepository.getExerciseSessionById(sessionId)
//            if (result.isSuccess) {
//                _exerciseSession.value = result.getOrNull()
//            }
//            _isLoading.value = false
//        }
//    }
//
//    private fun refreshDataAfterExerciseCompletion() {
//        loadExerciseSessions(forceRefresh = true)
//        loadDailyExerciseStats(forceRefresh = true)
//        loadWeeklyExerciseStats(forceRefresh = true)
//    }
//
//    fun resetAddExerciseResult() {
//        resetState()
//        _addExerciseResult.value = null
//    }
//
//    fun setSelectedSession(session: ExerciseSessionResponse) {
//        _exerciseSession.value = session
//    }
//
//}

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
