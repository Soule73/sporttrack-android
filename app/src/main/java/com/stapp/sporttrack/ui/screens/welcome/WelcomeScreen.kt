/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stapp.sporttrack.ui.screens.welcome

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.ExerciseSessionData
import com.stapp.sporttrack.data.HealthConnectAvailability
import com.stapp.sporttrack.ui.components.NotInstalledMessage
import com.stapp.sporttrack.ui.components.NotSupportedMessage
import com.stapp.sporttrack.ui.components.SummaryCard
import com.stapp.sporttrack.ui.theme.caloriesColor
import com.stapp.sporttrack.ui.theme.durationColor
import com.stapp.sporttrack.ui.theme.heartRateColor
import com.stapp.sporttrack.ui.theme.stepsColor
import java.util.UUID

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.stapp.sporttrack.ui.components.StepsBarChart

/**
 * Welcome screen shown when the app is first launched.
 */
@SuppressLint("DefaultLocale")
@Composable
fun WelcomeScreen(
    healthConnectAvailability: HealthConnectAvailability,
    onResumeAvailabilityCheck: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    modifier: Modifier,

    //
    permissions: Set<String>,
    permissionsGranted: Boolean,
    weeklyExerciseSummary: ExerciseSessionData?,
    uiState: WelcomeViewModel.UiState,
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    dailySteps: Map<String, Long>,
) {
    val textColor = MaterialTheme.colorScheme.primary.toArgb()
    val barColor = MaterialTheme.colorScheme.secondary.toArgb()

    val currentOnAvailabilityCheck by rememberUpdatedState(onResumeAvailabilityCheck)
    // Add a listener to re-check whether Health Connect has been installed each time the Welcome
    // screen is resumed: This ensures that if the user has been redirected to the Play store and
    // followed the onboarding flow, then when the app is resumed, instead of showing the message
    // to ask the user to install Health Connect, the app recognises that Health Connect is now
    // available and shows the appropriate welcome.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentOnAvailabilityCheck()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val configuration = LocalConfiguration.current

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is WelcomeViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [ExerciseSessionDetailViewModel.UiState] provides details of whether the last action
        // was a success or resulted in an error. Where an error occurred, for example in reading
        // and writing to Health Connect, the user is notified, and where the error is one that can
        // be recovered from, an attempt to do so is made.
        if (uiState is WelcomeViewModel.UiState.Error &&
            errorId.value != uiState.uuid
        ) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }
    if (uiState != WelcomeViewModel.UiState.Uninitialized) {
        LazyColumn(modifier =modifier) {
//            item {
//                Image(
//                    modifier = Modifier.fillMaxWidth(0.5f),
//                    painter = painterResource(id = R.drawable.ic_launcher_background),
//                    contentDescription = stringResource(id = R.string.health_connect_logo)
//                )
//                Spacer(modifier = Modifier.height(32.dp))
//                Text(
//                    text = stringResource(id = R.string.welcome_message),
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//                Spacer(modifier = Modifier.height(32.dp))
//            }
            when (healthConnectAvailability) {
                HealthConnectAvailability.INSTALLED -> item {

                    if (!permissionsGranted) {

                        Button(
                            onClick = { onPermissionsLaunch(permissions) }
                        ) {
                            Text(text = stringResource(R.string.permissions_button_label))
                        }

                    } else {
                        weeklyExerciseSummary?.let { summary ->

                            Text(
                                text = "Exercice hebdomadaire",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(6.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SummaryCard(
                                    title = "Total des pas",
                                    value = summary.totalSteps?.toString() ?: "N/A",
                                    icon = R.drawable.ic_directions_walk,
                                    containerColor = stepsColor,
                                    contentColor = stepsColor,
                                    modifier = Modifier
                                        .width(configuration.screenWidthDp.dp / 2)

                                )
                                SummaryCard(
                                    title = "Calories brûlées",
                                    value = summary.totalEnergyBurned?.inCalories?.let { String.format("%.2f", it) }
                                        ?: "N/A",
                                    unit = "kcal",
                                    icon = R.drawable.ic_local_dining,
                                    containerColor = caloriesColor,
                                    contentColor = caloriesColor,
                                    modifier = Modifier
                                        .width(configuration.screenWidthDp.dp / 2)

                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SummaryCard(
                                    title = "Durée totale",
                                    value = summary.totalActiveTime?.toMinutes()?.toString() ?: "N/A",
                                    unit = "min",
                                    icon = R.drawable.baseline_flash_on_24,
//                                    icon = R.drawable.ic_duration,
                                    containerColor = durationColor,
                                    contentColor = durationColor,
                                    modifier = Modifier
                                        .width(configuration.screenWidthDp.dp / 2)

                                )
                                SummaryCard(
                                    title = "Rythme cardiaque",
                                    value = summary.avgHeartRate?.toString() ?: "N/A",
                                    unit = "bpm",
                                    icon = R.drawable.ic_heart_rate,
                                    containerColor = heartRateColor,
                                    contentColor = heartRateColor,
                                    modifier = Modifier
                                        .width(configuration.screenWidthDp.dp / 2)

                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Ajouter le diagramme à barres pour les pas quotidiens
                            AndroidView(factory = {
                                StepsBarChart(it).apply {

                                    setData(dailySteps, textColor, barColor)
                                }
                            }, update = {

                                it.setData(dailySteps, textColor, barColor)
                            }, modifier = Modifier
                                .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.01f),
                                        shape = MaterialTheme.shapes.medium

                                    )
                                        .padding(16.dp)
                                .height(200.dp))
                        }
//                        InstalledMessage()
                    }
                }

                HealthConnectAvailability.NOT_INSTALLED -> item {
                    NotInstalledMessage()
                }

                HealthConnectAvailability.NOT_SUPPORTED -> item {
                    NotSupportedMessage()
                }
            }
        }
    }
//  Column(
//    modifier =modifier,
//    verticalArrangement = Arrangement.Top,
//    horizontalAlignment = Alignment.CenterHorizontally
//  ) {
//    Image(
//      modifier = Modifier.fillMaxWidth(0.5f),
//      painter = painterResource(id = R.drawable.ic_launcher_background),
//      contentDescription = stringResource(id = R.string.health_connect_logo)
//    )
//    Spacer(modifier = Modifier.height(32.dp))
//    Text(
//      text = stringResource(id = R.string.welcome_message),
//      color = MaterialTheme.colorScheme.onBackground
//    )
//    Spacer(modifier = Modifier.height(32.dp))
//    when (healthConnectAvailability) {
//      HealthConnectAvailability.INSTALLED -> InstalledMessage()
//      HealthConnectAvailability.NOT_INSTALLED -> NotInstalledMessage()
//      HealthConnectAvailability.NOT_SUPPORTED -> NotSupportedMessage()
//    }
//  }
}

//
//@Preview
//@Composable
//fun InstalledMessagePreview() {
//  SportTrackTheme {
//    WelcomeScreen(
//      healthConnectAvailability = HealthConnectAvailability.INSTALLED,
//      onResumeAvailabilityCheck = {},
//      modifier = Modifier
//    )
//  }
//}
//
//@Preview
//@Composable
//fun NotInstalledMessagePreview() {
//  SportTrackTheme {
//    WelcomeScreen(
//      healthConnectAvailability = HealthConnectAvailability.NOT_INSTALLED,
//      onResumeAvailabilityCheck = {},
//      modifier = Modifier
//    )
//  }
//}
//
//@Preview
//@Composable
//fun NotSupportedMessagePreview() {
//  SportTrackTheme {
//    WelcomeScreen(
//      healthConnectAvailability = HealthConnectAvailability.NOT_SUPPORTED,
//      onResumeAvailabilityCheck = {},
//      modifier = Modifier
//    )
//  }
//}
