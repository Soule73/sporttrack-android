package com.stapp.sporttrack.ui.screens.exercise

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.interfaces.exercice.ExerciseStrategy
import com.stapp.sporttrack.data.interfaces.exercice.RunningStrategy
import com.stapp.sporttrack.data.interfaces.exercice.StrengthTrainingStrategy
import com.stapp.sporttrack.data.interfaces.exercice.SwimmingStrategy
import com.stapp.sporttrack.data.interfaces.exercice.WalkingStrategy
import com.stapp.sporttrack.data.interfaces.exercice.YogaStrategy
import com.stapp.sporttrack.data.models.ExerciseStats
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import com.stapp.sporttrack.ui.components.AutoPauseSettingsBottomSheet
import com.stapp.sporttrack.ui.components.CountdownAnimation
import com.stapp.sporttrack.ui.components.ExerciseSessionSectionButtons
import com.stapp.sporttrack.ui.components.ExerciseSessionSummaryCardsList
import com.stapp.sporttrack.ui.components.MapCard
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.utils.distanceTo
import com.stapp.sporttrack.viewmodel.AuthViewModel
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale

@SuppressLint("MissingPermission")
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
    onClickFinished: () -> Unit
) {
    val configuration = LocalConfiguration.current

    val userData by authViewModel.userData.collectAsStateWithLifecycle()

    val isSessionActive by exerciseViewModel.isSessionActive.observeAsState(false)
    val isSessionPaused by exerciseViewModel.isSessionPaused.observeAsState(false)
    val elapsedTime by exerciseViewModel.elapsedTime.observeAsState(0L)
    val totalDistance by exerciseViewModel.totalDistance.observeAsState(0.0)
    val stepCount by exerciseViewModel.stepCount.observeAsState(0)
    val lastLocation by exerciseViewModel.lastLocation.observeAsState(null)
    val stats by exerciseViewModel.stats.observeAsState(ExerciseStats())
    val startTime by exerciseViewModel.startTime.observeAsState(0L)
    val isAutoPaused by exerciseViewModel.isAutoPaused.observeAsState(false)

    val path = remember { mutableStateListOf<LatLng>() }
    val parisLocation = LatLng(48.8566, 2.3522)  // Coordonnées de Paris
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(parisLocation, 12f)
    }

    var showSettingsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var ttsInitialized by remember { mutableStateOf(false) }
    val tts = remember {
        TextToSpeech(context) { status ->
            ttsInitialized = status == TextToSpeech.SUCCESS
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }


    val sharedPref = context.getSharedPreferences(
        SharedPreferencesConstants.PREF_NAME,
        Context.MODE_PRIVATE
    )
    val isAutoPauseEnabled = remember {
        mutableStateOf(
            sharedPref.getBoolean(
                SharedPreferencesConstants.AUTO_PAUSE_ENABLED,
                true
            )
        )
    }

    val exerciseStrategy: ExerciseStrategy = ExerciseUtils.getExerciseStrategy(exerciseType)

    val dark = isSystemInDarkTheme()
    val mapProperties by remember {
        mutableStateOf(ExerciseUtils.getMapProperties(context, dark))
    }

    // Gérer les permissions
    val listPermissions = ExerciseUtils.getPermissionsList(exerciseStrategy)
    val permissionsState = rememberMultiplePermissionsState(permissions = listPermissions)

    val allPermissionsGranted = permissionsState.allPermissionsGranted

    val shouldShowRationale = permissionsState.permissions.any { permissionState ->
        !permissionState.hasPermission && !permissionState.shouldShowRationale
    }

    val locationPermissionsGranted = permissionsState.permissions.any { permissionState ->
        (permissionState.permission == Manifest.permission.ACCESS_FINE_LOCATION ||
                permissionState.permission == Manifest.permission.ACCESS_COARSE_LOCATION) &&
                permissionState.hasPermission
    }

    val activityRecognitionPermissionGranted = permissionsState.permissions.any { permissionState ->
        permissionState.permission == Manifest.permission.ACTIVITY_RECOGNITION &&
                permissionState.hasPermission
    }

    LaunchedEffect(exerciseType) {
        exerciseViewModel.setExerciseType(exerciseType)
    }

    LaunchedEffect(Unit) {
        if (!allPermissionsGranted) {
            ExerciseUtils.requestPermissions(permissionsState)
        }
    }

    val summaryCardsData = remember(stats) {
        exerciseStrategy.getSummaryCardsData(stats)
    }

    // Fonctions de contrôle de la session
    fun startExerciseSession() {
        if (!allPermissionsGranted) {

            ExerciseUtils.requestPermissions(permissionsState)

            Toast.makeText(
                context,
                "Veuillez accorder les permissions requises pour démarrer la session.",
                Toast.LENGTH_LONG
            ).show()
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

    // Gestion du timer avec Flow
    LaunchedEffect(isSessionActive, isSessionPaused) {
        if (isSessionActive && !isSessionPaused) {
            // Utilisation d'un Flow pour émettre un tick toutes les secondes
            flow {
                while (true) {
                    emit(Unit)
                    delay(1000L)
                }
            }.collect {
                val currentTime = SystemClock.elapsedRealtime()
                val elapsed = currentTime - (startTime ?: currentTime)
                exerciseViewModel.elapsedTime.postValue(elapsed)

                val updatedStats = exerciseStrategy.updateExerciseStats(
                    userData?.weight,
                    elapsedTime,
                    totalDistance,
                    stepCount,
                    lastLocation,
                    null,
                )
                exerciseViewModel.stats.postValue(updatedStats)
            }
        }
    }

    // Gestion de la localisation
    val locationFlow: Flow<LatLng?>?
    if (exerciseStrategy.displayMap() && locationPermissionsGranted) {
        locationFlow = ExerciseUtils.createLocationFlow(context, fusedLocationClient)
        LaunchedEffect(isSessionActive, isSessionPaused) {
            if (isSessionActive && !isSessionPaused) {
                // Gestion des mises à jour de localisation
                locationFlow?.collect { newLocation ->
                    val previousLocation = lastLocation
                    exerciseViewModel.lastLocation.postValue(newLocation)

                    if (previousLocation != null && newLocation != null) {
                        val distance = previousLocation.distanceTo(newLocation)
                        exerciseViewModel.totalDistance.postValue(totalDistance + distance)
                        path.add(newLocation)
                        // Mettre à jour la position de la caméra
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(newLocation, 18f),
                            1_000
                        )
                    }

                    // Mettre à jour les statistiques
                    val updatedStats = exerciseStrategy.updateExerciseStats(
                        userData?.weight,
                        elapsedTime,
                        totalDistance,
                        stepCount,
                        previousLocation,
                        newLocation
                    )
                    exerciseViewModel.stats.postValue(updatedStats)
                }
            }
        }
    }

    // Gestion du capteur de pas
    var stepDetectorListener by remember { mutableStateOf<SensorEventListener?>(null) }

    fun speakMessage(message: String) {
        if (ttsInitialized) {
            if (tts.isLanguageAvailable(Locale.FRENCH) == LANG_AVAILABLE) {
                tts.language = Locale.FRENCH
            }
            tts.language = Locale.FRENCH

            tts.speak(message, TextToSpeech.QUEUE_ADD, null, null)
        }
    }
    LaunchedEffect(isAutoPaused, isAutoPauseEnabled.value) {
        if (exerciseStrategy.calculateSteps()) {

            if (isAutoPaused) {
                speakMessage("L'exercice a été mis en pause")
            } else if (isSessionActive && !isSessionPaused && !isSessionPaused) {
                speakMessage("L'exercice a repris")
            }
        }
    }

    LaunchedEffect(
        isSessionActive,
        isSessionPaused,
        isAutoPauseEnabled.value,
        activityRecognitionPermissionGranted
    ) {
        if (isSessionActive && exerciseStrategy.calculateSteps() && activityRecognitionPermissionGranted) {
            if (stepDetectorListener == null) {
                stepDetectorListener = ExerciseUtils.manageStepDetector(
                    context = context,
                    exerciseViewModel = exerciseViewModel,
                    resumeExerciseSession = ::resumeExerciseSession,
                    pauseExerciseSession = ::pauseExerciseSession,
                    isAutoPauseEnabled = isAutoPauseEnabled.value
                )
            }

        } else {
            // Désinscrire le capteur de pas
            ExerciseUtils.unregisterStepDetector(context, stepDetectorListener)
            stepDetectorListener = null
        }
    }

    var isMapLoaded by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(false) }

    if (showSettingsSheet) {
        AutoPauseSettingsBottomSheet(
            state = sheetState,
            isAutoPauseEnabled = isAutoPauseEnabled.value,
            onAutoPauseChanged = { isChecked ->
                isAutoPauseEnabled.value = isChecked
                // Enregistrer la préférence dans SharedPreferences
                with(sharedPref.edit()) {
                    putBoolean(
                        SharedPreferencesConstants.AUTO_PAUSE_ENABLED,
                        isChecked
                    )
                    apply()
                }
            },
            onDismissRequest = { showSettingsSheet = false }
        )
    }

    // Interface utilisateur en fonction des permissions
    if (!allPermissionsGranted) {
        if (shouldShowRationale) {
            // L'utilisateur a refusé de façon permanente certaines permissions
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Les permissions sont nécessaires pour cette fonctionnalité. Veuillez les activer dans les paramètres.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Ouvrir les paramètres de l'application
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Ouvrir les paramètres")
                }
            }
        } else {
            // Demander les permissions
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cette application nécessite des permissions pour fonctionner correctement.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    ExerciseUtils.requestPermissions(permissionsState)
                }) {
                    Text("Accorder les permissions")
                }
            }
        }
    } else {
        // Votre UI existante lorsque les permissions sont accordées
        Column(
            modifier = modifier
        ) {
            if (showCountdown) {
                CountdownAnimation(
                    onCountdownFinish = {

                        showCountdown = false
                        startExerciseSession()
                    }
                )
            } else {
                MapCard(
                    configuration = configuration,
                    displayMap = exerciseStrategy.displayMap(),
                    cameraPositionState = cameraPositionState,
                    mapProperties = mapProperties,
                    onMapLoaded = { isMapLoaded = true },
                    currentPostion = lastLocation,
                    path = path
                )
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
                    onClickSettings = { showSettingsSheet = true },
                )
            }
        }
    }
}

@Preview
@Composable
fun WalkingPreview() {

    val summaryCardsData = listOf(
        ExerciseSummaryCardData(
            title = "Durée",
            value = "30",
            unit = "min",
        ),
        ExerciseSummaryCardData(
            title = "Distance",
            value = "3.5",
            unit = "Km",
        ),
        ExerciseSummaryCardData(
            title = "Vitesse",
            value = "35",
            unit = "Km/h",

            ),
        ExerciseSummaryCardData(
            title = "Pas",
            value = "100",
        )
    )

    ExerciseDetailScreenPreview(
        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WALKING,
        isSessionActive = true,
        isSessionPaused = true,
        summaryCardsData = summaryCardsData
    )
}

@Preview
@Composable
fun YogaPreview() {
    val summaryCardsData = listOf(
        ExerciseSummaryCardData(
            title = "Durée",
            value = "30",
            unit = "min",
        ),
        ExerciseSummaryCardData(
            title = "Distance",
            value = "3.5",
            unit = "Km",
        ),
    )

    ExerciseDetailScreenPreview(
        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_YOGA,
        isSessionActive = false,
        isSessionPaused = false,
        summaryCardsData = summaryCardsData
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreenPreview(
    exerciseType: Int = ExerciseSessionRecord.EXERCISE_TYPE_WALKING,
    isSessionActive: Boolean,
    isSessionPaused: Boolean,
    summaryCardsData: List<ExerciseSummaryCardData>
) {
    val configuration = LocalConfiguration.current
    // Déterminer la stratégie appropriée en fonction du type d'exercice
    val exerciseStrategy: ExerciseStrategy = when (exerciseType) {
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> WalkingStrategy()
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> RunningStrategy()
        ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> SwimmingStrategy()
        ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> StrengthTrainingStrategy()
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA,
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL
            -> YogaStrategy()

        else -> WalkingStrategy() // Par défaut
    }

    SportTrackTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (exerciseType) {
                                ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Marche"
                                ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Course"
                                ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Vélo"
                                ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> "Nage piscine"
                                ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "Randonnée"
                                ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> "Musculation"
                                ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "Yoga"
                                ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL -> "Course sur Tapis"
                                else -> "Autre"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.baseline_chevron_left_24
                                ),
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier.padding(padding)
                ) {
                    // Dynamically show map based on exercise type
                    if (exerciseStrategy.displayMap()) {
                        Box(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((configuration.screenHeightDp.dp / 2))
                                    .clip(MaterialTheme.shapes.extraLarge),
                            ) {

                            }
                        }
                    }
                    ExerciseSessionSummaryCardsList(
                        cardsData = summaryCardsData,
                        configuration = configuration
                    )
                    ExerciseSessionSectionButtons(
                        isSessionActive = isSessionActive,
                        isSessionPaused = isSessionPaused,
                        onClickPaused = { },
                        onClickResume = { },
                        onClickStart = { },
                        onClickFinished = { },
                        onClickSettings = { },
                        configuration = configuration,
                    )
                }
            }
        )
    }
}

