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
package com.stapp.sporttrack.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.HealthConnectAvailability
import com.stapp.sporttrack.data.HealthConnectManager
import com.stapp.sporttrack.ui.navigation.Drawer
import com.stapp.sporttrack.ui.navigation.HealthConnectNavigation
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.getUserData
import com.stapp.sporttrack.utils.saveUserData
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val TAG = "Health Connect Codelab"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter",
    "RememberReturnType"
)
@Composable
fun HealthConnectApp(
    healthConnectManager: HealthConnectManager,
    context: Context,
    verifyToken: Boolean,
    registrationViewModel: RegistrationViewModel,
) {

    val userData = getUserData(context)
    var tokenVerified by remember { mutableStateOf(false) }
    var verificationAttempted by remember { mutableStateOf(false) }

    LaunchedEffect(registrationViewModel) {
        if (verifyToken && !verificationAttempted) {
            verificationAttempted = true
            registrationViewModel.verifyToken()
            registrationViewModel.verifyTokenResult.collectLatest { result ->
                result?.onSuccess { userResponse ->
                    saveUserData(context, userResponse)
                    tokenVerified = true
                }?.onFailure { exception ->
                    tokenVerified = true
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    SportTrackTheme {
        val scaffoldState = remember { SnackbarHostState() }
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val availability by healthConnectManager.availability

        ModalNavigationDrawer(
            scrimColor = Color.Black,
            drawerState = drawerState,
            drawerContent = {
                if (availability == HealthConnectAvailability.INSTALLED) {
                    Drawer(
                        context = context,
                        scope = scope,
                        drawerState = drawerState,
                        navController = navController,
                        registrationViewModel = registrationViewModel
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
//                        colors = TopAppBarDefaults.topAppBarColors(
//                            containerColor = MaterialTheme.colorScheme.primaryContainer,
//                            titleContentColor = MaterialTheme.colorScheme.primary,
//                        ),
                        title = {
                            val titleId = when (currentRoute) {
                                Screen.ExerciseSessions.route -> Screen.ExerciseSessions.titleId
                                Screen.InputReadings.route -> Screen.InputReadings.titleId
                                Screen.DifferentialChanges.route -> Screen.DifferentialChanges.titleId
                                else -> R.string.app_name
                            }
                            Text(stringResource(titleId))
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    if (availability == HealthConnectAvailability.INSTALLED) {
                                        scope.launch {
                                            if (drawerState.isClosed) {
                                                drawerState.open()
                                            } else {
                                                drawerState.close()
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Menu,
                                    stringResource(id = R.string.menu)
                                )
                            }
                        }
                    )
                },

                snackbarHost = {
                    SnackbarHost(hostState = scaffoldState) { data ->
                        Snackbar(snackbarData = data)
                    }
                },
                content = { paddingValues ->
                    HealthConnectNavigation(
                        healthConnectManager = healthConnectManager,
                        navController = navController,
                        scaffoldState = scaffoldState,
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(top = 16.dp)
                            .padding(horizontal = 10.dp)

                    )
                }
            )
        }
    }
}


