package com.stapp.sporttrack.ui.screens.exercise

import android.Manifest
import android.content.Context
import android.content.Intent
import android.hardware.SensorEventListener
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.LANG_AVAILABLE
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.stapp.sporttrack.data.interfaces.exercice.ExerciseStrategy
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.SharedExerciseState
import com.stapp.sporttrack.ui.components.AutoPauseSettingsBottomSheet
import com.stapp.sporttrack.ui.components.CountdownAnimation
import com.stapp.sporttrack.ui.components.ExerciseSessionSectionButtons
import com.stapp.sporttrack.ui.components.ExerciseSessionSummaryCardsList
import com.stapp.sporttrack.ui.components.MapCard
import com.stapp.sporttrack.ui.components.MissingPermission
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.utils.distanceTo
import com.stapp.sporttrack.viewmodel.AuthViewModel
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ExerciseSessionScreen(
    context: Context,
    exerciseType: Int,
    exerciseViewModel: ExerciseViewModel,
    authViewModel: AuthViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    modifier: Modifier,
    onClickFinished: () -> Unit,
    darkTheme: Boolean
)
{
    val configuration = LocalConfiguration.current
    val userData by authViewModel.userData.collectAsStateWithLifecycle()

    val isSessionActive by remember { derivedStateOf { SharedExerciseState.isSessionActive } }
    val isSessionPaused by remember { derivedStateOf { SharedExerciseState.isSessionPaused } }
    val elapsedTime by remember { derivedStateOf { SharedExerciseState.elapsedTime } }
    val totalDistance by remember { derivedStateOf { SharedExerciseState.totalDistance } }
    val stepCount by remember { derivedStateOf { SharedExerciseState.stepCount } }
    val lastLocation by remember { derivedStateOf { SharedExerciseState.lastLocation } }
    val stats by remember { derivedStateOf { SharedExerciseState.stats } }
    val startTime by remember { derivedStateOf { SharedExerciseState.startTime } }

    val exerciseStrategy: ExerciseStrategy = ExerciseUtils.getExerciseStrategy(exerciseType)

    val path = if (exerciseStrategy.displayMap()) remember { mutableStateListOf<LatLng>() } else null
    val cameraPositionState = if (exerciseStrategy.displayMap()) {
        val initialLocation = LatLng(48.8566, 2.3522)
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
        }
    } else null

    var isMapLoaded by remember { mutableStateOf(false) }
    val mapProperties by remember {
        mutableStateOf(
            if (exerciseStrategy.displayMap()) ExerciseUtils.getMapProperties(context, darkTheme) else null
        )
    }

    var stepDetectorListener by remember { mutableStateOf<SensorEventListener?>(null) }
    var ttsInitialized by remember { mutableStateOf(false) }
    val tts = remember {
        TextToSpeech(context) { status ->
            ttsInitialized = (status == TextToSpeech.SUCCESS)
        }
    }

    val sharedPref = context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
    val isAutoPauseEnabled = remember {
        mutableStateOf(sharedPref.getBoolean(SharedPreferencesConstants.AUTO_PAUSE_ENABLED, true))
    }

    // Gestion des permissions
    val listPermissions = ExerciseUtils.getPermissionsList()
    val permissionsState = rememberMultiplePermissionsState(permissions = listPermissions)
    val allPermissionsGranted = permissionsState.allPermissionsGranted
    val shouldShowRationale = permissionsState.permissions.any { !it.hasPermission && !it.shouldShowRationale }
    val locationPermissionsGranted = permissionsState.permissions.any {
        (it.permission == Manifest.permission.ACCESS_FINE_LOCATION || it.permission == Manifest.permission.ACCESS_COARSE_LOCATION) && it.hasPermission
    }
    val activityRecognitionPermissionGranted = permissionsState.permissions.any {
        it.permission == Manifest.permission.ACTIVITY_RECOGNITION && it.hasPermission
    }

    LaunchedEffect(exerciseType) {
        exerciseViewModel.setExerciseType(exerciseType)
    }

    LaunchedEffect(Unit) {
        if (!allPermissionsGranted) {
            ExerciseUtils.requestPermissions(permissionsState)
        }
    }

    val summaryCardsData = remember(stats) { exerciseStrategy.getSummaryCardsData(stats) }

    // Fonctions de contrôle de la session
    fun startExerciseSession() {
        if (!allPermissionsGranted) {
            ExerciseUtils.requestPermissions(permissionsState)
            Toast.makeText(context, "Veuillez accorder les permissions requises pour démarrer la session.", Toast.LENGTH_LONG).show()
            return
        }
        if (isSessionActive) return
        exerciseViewModel.startExerciseSession(context, userData?.userId)
    }

    fun pauseExerciseSession(autoPause: Boolean = false) {
        exerciseViewModel.pauseExerciseSession(autoPause)
    }

    fun resumeExerciseSession() {
        if (!isSessionPaused) return
        exerciseViewModel.resumeExerciseSession()
    }

    fun stopExerciseSession() {
        exerciseViewModel.stopExerciseSession(context)
    }

    // Gestion du timer : mise à jour de l'état partagé
    LaunchedEffect(isSessionActive, isSessionPaused) {
        if (isSessionActive && !isSessionPaused) {
            flow {
                while (true) {
                    emit(Unit)
                    delay(1000L)
                }
            }.collect {
                val currentTime = SystemClock.elapsedRealtime()
                val elapsed = currentTime - (startTime ?: currentTime)
                SharedExerciseState.elapsedTime = elapsed

                val updatedStats = exerciseStrategy.updateExerciseStats(
                    userData?.weight ?: SharedExerciseState.userWeight,
                    elapsedTime,
                    totalDistance,
                    stepCount,
                    lastLocation,
                    null
                )
                SharedExerciseState.stats = updatedStats
            }
        }
    }

    // Gestion de la localisation
    var locationFlow: Job? by remember { mutableStateOf(null) }
    LaunchedEffect(isSessionActive, isSessionPaused, exerciseStrategy.displayMap()) {
        locationFlow?.cancel()
        locationFlow = null
        if (isSessionActive && !isSessionPaused && exerciseStrategy.displayMap() && locationPermissionsGranted) {
            locationFlow = launch {
                ExerciseUtils.createLocationFlow(context, fusedLocationClient)?.collect { newLocation ->
                    val previousLocation = lastLocation
                    SharedExerciseState.lastLocation = newLocation
                    if (previousLocation != null && newLocation != null) {
                        val distance = previousLocation.distanceTo(newLocation)
                        SharedExerciseState.totalDistance += distance
                        path?.add(newLocation)
                        cameraPositionState?.animate(CameraUpdateFactory.newLatLngZoom(newLocation, 18f), 1000)
                    }
                    val updatedStats = exerciseStrategy.updateExerciseStats(
                        userData?.weight,
                        elapsedTime,
                        totalDistance,
                        stepCount,
                        previousLocation,
                        newLocation
                    )
                    SharedExerciseState.stats = updatedStats
                }
            }
        }
    }

    // TTS pour notifications d'auto-pause
    fun speakMessage(message: String) {
        if (ttsInitialized) {
            if (tts.isLanguageAvailable(Locale.FRENCH) == LANG_AVAILABLE) {
                tts.language = Locale.FRENCH
            }
            tts.speak(message, TextToSpeech.QUEUE_ADD, null, null)
        }
    }
    fun onAutoPause() = speakMessage("L'exercice a été mis en pause")
    fun onAutoResume() = speakMessage("L'exercice a repris")

    // Gestion du détecteur de pas
    LaunchedEffect(isSessionActive, isSessionPaused, isAutoPauseEnabled.value, activityRecognitionPermissionGranted) {
        if (isSessionActive && exerciseStrategy.calculateSteps() && activityRecognitionPermissionGranted) {
            if (stepDetectorListener == null) {
                stepDetectorListener = ExerciseUtils.manageStepDetector(
                    context = context,
                    resumeExerciseSession = ::resumeExerciseSession,
                    pauseExerciseSession = ::pauseExerciseSession,
                    isAutoPauseEnabled = isAutoPauseEnabled.value,
                    onAutoPause = ::onAutoPause,
                    onAutoResume = ::onAutoResume
                )
            }
        } else {
            ExerciseUtils.unregisterStepDetector(context, stepDetectorListener)
            stepDetectorListener = null
        }
    }

    var showSettingsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCountdown by remember { mutableStateOf(false) }

    if (showSettingsSheet) {
        AutoPauseSettingsBottomSheet(
            state = sheetState,
            isAutoPauseEnabled = isAutoPauseEnabled.value,
            onAutoPauseChanged = { isChecked ->
                isAutoPauseEnabled.value = isChecked
                with(sharedPref.edit()) {
                    putBoolean(SharedPreferencesConstants.AUTO_PAUSE_ENABLED, isChecked)
                    apply()
                }
            },
            onDismissRequest = { showSettingsSheet = false }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
            locationFlow?.cancel()
            locationFlow = null
        }
    }

    if (!allPermissionsGranted) {
        if (shouldShowRationale) {
            MissingPermission(
                title = "Les permissions sont nécessaires pour cette fonctionnalité. Veuillez les activer dans les paramètres.",
                modifier = modifier,
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                btnTitle = "Ouvrir les paramètres"
            )
        } else {
            MissingPermission(
                title = "Les permissions sont nécessaires pour cette fonctionnalité.",
                modifier = modifier,
                onClick = { ExerciseUtils.requestPermissions(permissionsState) },
                btnTitle = "Accorder les permissions"
            )
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            if (showCountdown) {
                CountdownAnimation(
                    onCountdownFinish = {
                        showCountdown = false
                        startExerciseSession()
                    }
                )
            } else {
                if (exerciseStrategy.displayMap()) {
                    MapCard(
                        configuration = configuration,
                        displayMap = true,
                        cameraPositionState = cameraPositionState!!,
                        mapProperties = mapProperties!!,
                        onMapLoaded = { isMapLoaded = true },
                        userLocation = lastLocation,
                        path = path
                    )
                }
                ExerciseSessionSummaryCardsList(
                    configuration = configuration,
                    cardsData = summaryCardsData
                )
                ExerciseSessionSectionButtons(
                    configuration = configuration,
                    isSessionActive = isSessionActive,
                    isSessionPaused = isSessionPaused,
                    onClickStart = { showCountdown = true },
                    onClickPaused = { pauseExerciseSession() },
                    onClickResume = { resumeExerciseSession() },
                    onClickFinished = {
                        stopExerciseSession()
                        onClickFinished()
                    },
                    onClickSettings = { showSettingsSheet = true }
                )
            }
        }
    }
}
//fun ExerciseSessionScreen(
//    context: Context,
//    exerciseType: Int,
//    exerciseViewModel: ExerciseViewModel,
//    authViewModel: AuthViewModel,
//    fusedLocationClient: FusedLocationProviderClient,
//    modifier: Modifier,
//    onClickFinished: () -> Unit,
//    darkTheme: Boolean
//) {
//    val configuration = LocalConfiguration.current
//
//    val userData by authViewModel.userData.collectAsStateWithLifecycle()
//
//    val isSessionActive by exerciseViewModel.isSessionActive.observeAsState(false)
//    val isSessionPaused by exerciseViewModel.isSessionPaused.observeAsState(false)
//    val elapsedTime by exerciseViewModel.elapsedTime.observeAsState(0L)
//    val totalDistance by exerciseViewModel.totalDistance.observeAsState(0.0)
//    val stepCount by exerciseViewModel.stepCount.observeAsState(0)
//    val lastLocation by exerciseViewModel.lastLocation.observeAsState(null)
//    val stats by exerciseViewModel.stats.observeAsState(ExerciseStats())
//    val startTime by exerciseViewModel.startTime.observeAsState(0L)
//
//    val exerciseStrategy: ExerciseStrategy = ExerciseUtils.getExerciseStrategy(exerciseType)
//
//    // Initialize variables related to the map only if the exercise requires a map
//    val path =
//        if (exerciseStrategy.displayMap()) remember { mutableStateListOf<LatLng>() } else null
//    val cameraPositionState = if (exerciseStrategy.displayMap()) {
//        val initialLocation = LatLng(48.8566, 2.3522) // Defaults to Paris
//        rememberCameraPositionState {
//            position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
//        }
//    } else null
//
//    // Map properties and loading state
//    var isMapLoaded by remember { mutableStateOf(false) }
//
//    val mapProperties by remember {
//        mutableStateOf(
//            if (exerciseStrategy.displayMap()) {
//                ExerciseUtils.getMapProperties(context, darkTheme)
//            } else {
//                null
//            }
//        )
//    }
//
//    var stepDetectorListener by remember { mutableStateOf<SensorEventListener?>(null) }
//
//    var ttsInitialized by remember { mutableStateOf(false) }
//    val tts = remember {
//        TextToSpeech(context) { status ->
//            ttsInitialized = status == TextToSpeech.SUCCESS
//        }
//    }
//
//    val sharedPref = context.getSharedPreferences(
//        SharedPreferencesConstants.PREF_NAME,
//        Context.MODE_PRIVATE
//    )
//
//    val isAutoPauseEnabled = remember {
//        mutableStateOf(
//            sharedPref.getBoolean(
//                SharedPreferencesConstants.AUTO_PAUSE_ENABLED,
//                true
//            )
//        )
//    }
//
//    // Permissions handling
//    val listPermissions = ExerciseUtils.getPermissionsList()
//    val permissionsState = rememberMultiplePermissionsState(permissions = listPermissions)
//
//    val allPermissionsGranted = permissionsState.allPermissionsGranted
//
//    val shouldShowRationale = permissionsState.permissions.any { permissionState ->
//        !permissionState.hasPermission && !permissionState.shouldShowRationale
//    }
//
//    val locationPermissionsGranted = permissionsState.permissions.any { permissionState ->
//        (permissionState.permission == Manifest.permission.ACCESS_FINE_LOCATION ||
//                permissionState.permission == Manifest.permission.ACCESS_COARSE_LOCATION) &&
//                permissionState.hasPermission
//    }
//
//    val activityRecognitionPermissionGranted = permissionsState.permissions.any { permissionState ->
//        permissionState.permission == Manifest.permission.ACTIVITY_RECOGNITION &&
//                permissionState.hasPermission
//    }
//
//    // Set the exercise type in the ViewModel
//    LaunchedEffect(exerciseType) {
//        exerciseViewModel.setExerciseType(exerciseType)
//    }
//
//    // Request permissions if not granted
//    LaunchedEffect(Unit) {
//        if (!allPermissionsGranted) {
//            ExerciseUtils.requestPermissions(permissionsState)
//        }
//    }
//
//    val summaryCardsData = remember(stats) {
//        exerciseStrategy.getSummaryCardsData(stats)
//    }
//
//    // Session control functions
//    fun startExerciseSession() {
//        if (!allPermissionsGranted) {
//            ExerciseUtils.requestPermissions(permissionsState)
//            Toast.makeText(
//                context,
//                "Veuillez accorder les permissions requises pour démarrer la session.",
//                Toast.LENGTH_LONG
//            ).show()
//            return
//        }
//
//        if (isSessionActive) return
//
//        exerciseViewModel.startExerciseSession(context, userData?.userId)
//    }
//
//    fun pauseExerciseSession(autoPause: Boolean = false) {
//        exerciseViewModel.pauseExerciseSession(autoPause)
//    }
//
//    fun resumeExerciseSession() {
//        if (!isSessionPaused) return
//
//        exerciseViewModel.resumeExerciseSession()
//    }
//
//    fun stopExerciseSession() {
//        exerciseViewModel.stopExerciseSession(context)
//    }
//
//    // Timer management
//    LaunchedEffect(isSessionActive, isSessionPaused) {
//        if (isSessionActive && !isSessionPaused) {
//
//            flow {
//                while (true) {
//                    emit(Unit)
//                    delay(1000L)
//                }
//            }.collect {
//                val currentTime = SystemClock.elapsedRealtime()
//                val elapsed = currentTime - (startTime ?: currentTime)
//                exerciseViewModel.elapsedTime.postValue(elapsed)
//
//                val updatedStats = exerciseStrategy.updateExerciseStats(
//                    userData?.weight,
//                    elapsedTime,
//                    totalDistance,
//                    stepCount,
//                    lastLocation,
//                    null,
//                )
//                exerciseViewModel.stats.postValue(updatedStats)
//            }
//        }
//    }
//
//    // Location management
//    var locationFlow: Job? by remember { mutableStateOf(null) }
//
//    LaunchedEffect(isSessionActive, isSessionPaused, exerciseStrategy.displayMap()) {
//        locationFlow?.cancel()
//        locationFlow = null
//
//        if (isSessionActive && !isSessionPaused && exerciseStrategy.displayMap() && locationPermissionsGranted) {
//            locationFlow = launch {
//                ExerciseUtils.createLocationFlow(context, fusedLocationClient)
//                    ?.collect { newLocation ->
//
//                        val previousLocation = lastLocation
//                        exerciseViewModel.lastLocation.postValue(newLocation)
//
//                        if (previousLocation != null && newLocation != null) {
//                            val distance = previousLocation.distanceTo(newLocation)
//                            exerciseViewModel.totalDistance.postValue(totalDistance + distance)
//                            path?.add(newLocation)
//
//                            cameraPositionState?.animate(
//                                CameraUpdateFactory.newLatLngZoom(newLocation, 18f),
//                                1_000
//                            )
//                        }
//
//                        val updatedStats = exerciseStrategy.updateExerciseStats(
//                            userData?.weight,
//                            elapsedTime,
//                            totalDistance,
//                            stepCount,
//                            previousLocation,
//                            newLocation
//                        )
//                        exerciseViewModel.stats.postValue(updatedStats)
//                    }
//            }
//        }
//    }
//
//    // TTS for auto-pause notifications
//    fun speakMessage(message: String) {
//        if (ttsInitialized) {
//            if (tts.isLanguageAvailable(Locale.FRENCH) == LANG_AVAILABLE) {
//                tts.language = Locale.FRENCH
//            }
//            tts.speak(message, TextToSpeech.QUEUE_ADD, null, null)
//        }
//    }
//
//    fun onAutoPause() {
//        speakMessage("L'exercice a été mis en pause")
//    }
//
//    fun onAutoResume() {
//        speakMessage("L'exercice a repris")
//    }
//
//    // Step detector management
//    LaunchedEffect(
//        isSessionActive,
//        isSessionPaused,
//        isAutoPauseEnabled.value,
//        activityRecognitionPermissionGranted
//    ) {
//        if (isSessionActive && exerciseStrategy.calculateSteps() && activityRecognitionPermissionGranted) {
//            if (stepDetectorListener == null) {
//                stepDetectorListener = ExerciseUtils.manageStepDetector(
//                    context = context,
//                    exerciseViewModel = exerciseViewModel,
//                    resumeExerciseSession = ::resumeExerciseSession,
//                    pauseExerciseSession = ::pauseExerciseSession,
//                    isAutoPauseEnabled = isAutoPauseEnabled.value,
//                    onAutoPause = ::onAutoPause,
//                    onAutoResume = ::onAutoResume,
//
//                    )
//            }
//        } else {
//            // Unregister the step detector
//            ExerciseUtils.unregisterStepDetector(context, stepDetectorListener)
//            stepDetectorListener = null
//        }
//    }
//
//    var showSettingsSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    var showCountdown by remember { mutableStateOf(false) }
//
//    if (showSettingsSheet) {
//        AutoPauseSettingsBottomSheet(
//            state = sheetState,
//            isAutoPauseEnabled = isAutoPauseEnabled.value,
//            onAutoPauseChanged = { isChecked ->
//                isAutoPauseEnabled.value = isChecked
//
//                with(sharedPref.edit()) {
//                    putBoolean(
//                        SharedPreferencesConstants.AUTO_PAUSE_ENABLED,
//                        isChecked
//                    )
//                    apply()
//                }
//            },
//            onDismissRequest = { showSettingsSheet = false }
//        )
//    }
//
//
//    DisposableEffect(Unit) {
//        onDispose {
//            tts.shutdown()
//            locationFlow?.cancel()
//            locationFlow = null
//        }
//    }
//
//    if (!allPermissionsGranted) {
//        if (shouldShowRationale) {
//
//            MissingPermission(
//                title = "Les permissions sont nécessaires pour cette fonctionnalité. Veuillez les activer dans les paramètres.",
//                modifier = modifier, onClick = {
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                        data = Uri.fromParts("package", context.packageName, null)
//                    }
//                    context.startActivity(intent)
//                },
//                btnTitle = "Ouvrir les paramètres"
//            )
//        } else {
//            MissingPermission(
//                title = "Les permissions sont nécessaires pour cette fonctionnalité.",
//                modifier = modifier,
//                onClick = { ExerciseUtils.requestPermissions(permissionsState) },
//                btnTitle = "Accorder les permissions"
//            )
//        }
//    } else {
//        Column(
//            modifier = modifier.fillMaxSize()
//        ) {
//            if (showCountdown) {
//                CountdownAnimation(
//                    onCountdownFinish = {
//                        showCountdown = false
//                        startExerciseSession()
//                    }
//                )
//            } else {
//                if (exerciseStrategy.displayMap()) {
//                    MapCard(
//                        configuration = configuration,
//                        displayMap = true,
//                        cameraPositionState = cameraPositionState!!,
//                        mapProperties = mapProperties!!,
//                        onMapLoaded = { isMapLoaded = true },
//                        userLocation = lastLocation,
//                        path = path
//                    )
//                }
//
//                // Summary cards
//                ExerciseSessionSummaryCardsList(
//                    configuration = configuration,
//                    cardsData = summaryCardsData
//                )
//
//                // Session control buttons
//                ExerciseSessionSectionButtons(
//                    configuration = configuration,
//                    isSessionActive = isSessionActive,
//                    isSessionPaused = isSessionPaused,
//                    onClickStart = { showCountdown = true },
//                    onClickPaused = { pauseExerciseSession() },
//                    onClickResume = { resumeExerciseSession() },
//                    onClickFinished = {
//                        stopExerciseSession()
//                        onClickFinished()
//                    },
//                    onClickSettings = { showSettingsSheet = true },
//                )
//            }
//        }
//    }
//}


