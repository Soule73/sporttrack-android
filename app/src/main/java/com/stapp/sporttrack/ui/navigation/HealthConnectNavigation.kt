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
package com.stapp.sporttrack.ui.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.stapp.sporttrack.data.HealthConnectManager
import com.stapp.sporttrack.showExceptionSnackbar
import com.stapp.sporttrack.ui.screens.welcome.WelcomeScreen
import com.stapp.sporttrack.ui.screens.changes.DifferentialChangesScreen
import com.stapp.sporttrack.ui.screens.changes.DifferentialChangesViewModel
import com.stapp.sporttrack.ui.screens.changes.DifferentialChangesViewModelFactory
import com.stapp.sporttrack.ui.screens.exercisesession.ExerciseSessionScreen
import com.stapp.sporttrack.ui.screens.exercisesession.ExerciseSessionViewModel
import com.stapp.sporttrack.ui.screens.exercisesession.ExerciseSessionViewModelFactory
import com.stapp.sporttrack.ui.screens.exercisesessiondetail.ExerciseSessionDetailScreen
import com.stapp.sporttrack.ui.screens.exercisesessiondetail.ExerciseSessionDetailViewModel
import com.stapp.sporttrack.ui.screens.exercisesessiondetail.ExerciseSessionDetailViewModelFactory
import com.stapp.sporttrack.ui.screens.inputreadings.InputReadingsScreen
import com.stapp.sporttrack.ui.screens.inputreadings.InputReadingsViewModel
import com.stapp.sporttrack.ui.screens.inputreadings.InputReadingsViewModelFactory
import com.stapp.sporttrack.ui.screens.privacypolicy.PrivacyPolicyScreen
import com.stapp.sporttrack.ui.screens.welcome.WelcomeViewModel
import com.stapp.sporttrack.ui.screens.welcome.WelcomeViewModelFactory

/**
 * Provides the navigation in the app.
 */
@Composable
fun HealthConnectNavigation(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager,
    scaffoldState: SnackbarHostState,
    modifier: Modifier,
) {

    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {
        val availability by healthConnectManager.availability
        composable(Screen.WelcomeScreen.route) {
            val viewModel: WelcomeViewModel = viewModel(
                factory = WelcomeViewModelFactory(
                    healthConnectManager = healthConnectManager
                )
            )
            val permissionsGranted by viewModel.permissionsGranted
            val permissions = viewModel.permissions
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            val weeklyExerciseSummary by viewModel.weeklyExerciseSummary
            val dailySteps by viewModel.dailySteps.collectAsState()
            WelcomeScreen(
                modifier = modifier,
                healthConnectAvailability = availability,
                onResumeAvailabilityCheck = {
                    healthConnectManager.checkAvailability()
                },

                //
                weeklyExerciseSummary =weeklyExerciseSummary,
                dailySteps =dailySteps,
                permissions = permissions,
                permissionsGranted = permissionsGranted,
                uiState = viewModel.uiState,
                onError = { exception ->
                    showExceptionSnackbar(scaffoldState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(
            route = Screen.PrivacyPolicy.route,
            deepLinks = listOf(
                navDeepLink {
                    action = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
                }
            )
        ) {
            PrivacyPolicyScreen(
                modifier = modifier,

                )
        }
        composable(Screen.ExerciseSessions.route) {
            val viewModel: ExerciseSessionViewModel = viewModel(
                factory = ExerciseSessionViewModelFactory(
                    healthConnectManager = healthConnectManager
                )
            )
            val permissionsGranted by viewModel.permissionsGranted
            val sessionsList by viewModel.sessionsList
            val permissions = viewModel.permissions
            val backgroundReadPermissions = viewModel.backgroundReadPermissions
            val backgroundReadAvailable by viewModel.backgroundReadAvailable
            val backgroundReadGranted by viewModel.backgroundReadGranted
            val historyReadPermissions = viewModel.historyReadPermissions
            val historyReadAvailable by viewModel.historyReadAvailable
            val historyReadGranted by viewModel.historyReadGranted
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            ExerciseSessionScreen(
                modifier = modifier,
                permissionsGranted = permissionsGranted,
                permissions = permissions,
                backgroundReadAvailable = backgroundReadAvailable,
                backgroundReadGranted = backgroundReadGranted,
                backgroundReadPermissions = backgroundReadPermissions,
                historyReadAvailable = historyReadAvailable,
                historyReadGranted = historyReadGranted,
                historyReadPermissions = historyReadPermissions,
                onBackgroundReadClick = {
                    viewModel.enqueueReadStepWorker()
                },
                sessionsList = sessionsList,
                uiState = viewModel.uiState,
                onInsertClick = {
                    viewModel.insertExerciseSession()
                },
                onDetailsClick = { uid ->
                    navController.navigate(Screen.ExerciseSessionDetail.route + "/" + uid)
                },
                onError = { exception ->
                    showExceptionSnackbar(scaffoldState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(Screen.ExerciseSessionDetail.route + "/{$UID_NAV_ARGUMENT}") {
            val uid = it.arguments?.getString(UID_NAV_ARGUMENT)!!
            val viewModel: ExerciseSessionDetailViewModel = viewModel(
                factory = ExerciseSessionDetailViewModelFactory(
                    uid = uid,
                    healthConnectManager = healthConnectManager
                )
            )
            val permissionsGranted by viewModel.permissionsGranted
            val sessionMetrics by viewModel.sessionMetrics
            val permissions = viewModel.permissions
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            ExerciseSessionDetailScreen(
                modifier = modifier,

                permissions = permissions,
                permissionsGranted = permissionsGranted,
                sessionMetrics = sessionMetrics,
                uiState = viewModel.uiState,
                onError = { exception ->
                    showExceptionSnackbar(scaffoldState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(Screen.InputReadings.route) {
            val viewModel: InputReadingsViewModel = viewModel(
                factory = InputReadingsViewModelFactory(
                    healthConnectManager = healthConnectManager
                )
            )
            val permissionsGranted by viewModel.permissionsGranted
            val readingsList by viewModel.readingsList
            val permissions = viewModel.permissions
            val weeklyAvg by viewModel.weeklyAvg
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            InputReadingsScreen(
                modifier = modifier,

                permissionsGranted = permissionsGranted,
                permissions = permissions,

                uiState = viewModel.uiState,
                onInsertClick = { weightInput ->
                    viewModel.inputReadings(weightInput)
                },
                weeklyAvg = weeklyAvg,
                readingsList = readingsList,
                onError = { exception ->
                    showExceptionSnackbar(scaffoldState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(Screen.DifferentialChanges.route) {
            val viewModel: DifferentialChangesViewModel = viewModel(
                factory = DifferentialChangesViewModelFactory(
                    healthConnectManager = healthConnectManager
                )
            )
            val changesToken by viewModel.changesToken
            val permissionsGranted by viewModel.permissionsGranted
            val permissions = viewModel.permissions
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            DifferentialChangesScreen(
                modifier = modifier,

                permissionsGranted = permissionsGranted,
                permissions = permissions,
                changesEnabled = changesToken != null,
                onChangesEnable = { enabled ->
                    viewModel.enableOrDisableChanges(enabled)
                },
                changes = viewModel.changes,
                changesToken = changesToken,
                onGetChanges = {
                    viewModel.getChanges()
                },
                uiState = viewModel.uiState,
                onError = { exception ->
                    showExceptionSnackbar(scaffoldState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
    }
}
