
package com.stapp.sporttrack.ui.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.MainViewModel
import com.stapp.sporttrack.ui.navigation.HealthConnectNavigation
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.theme.SettingsViewModel
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.AuthUtils
import com.stapp.sporttrack.viewmodel.AuthViewModel
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectApp(
    context: Context,
    verifyToken: Boolean,
    authViewModel: AuthViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    exerciseViewModel: ExerciseViewModel,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    startDestination: String,
    isAuthenticated: Boolean,
    settingsViewModel: SettingsViewModel,
    darkTheme: Boolean
) {

    var tokenVerified by remember { mutableStateOf(false) }
    var verificationAttempted by remember { mutableStateOf(false) }
    var appBarTitle by remember { mutableStateOf("") }
    var showBackBtn by remember { mutableStateOf(false) }
    var showAppBar by remember { mutableStateOf(true) }
    var showBottomAppBar by remember { mutableStateOf(true) }
    var isLargeAppBar by remember { mutableStateOf(true) }
    val scaffoldState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    // Écouter les commandes de navigation
    LaunchedEffect(mainViewModel) {
        mainViewModel.navigationCommands.collect { destination ->
            println("receive destination on health connect app $destination")

            if (destination != null) {
                navController.navigate(destination)
            }
        }
    }

    LaunchedEffect(authViewModel) {
        if (verifyToken && !verificationAttempted) {
            verificationAttempted = true
            authViewModel.verifyToken()
            authViewModel.verifyTokenResult.collectLatest { result ->
                result?.onSuccess { userResponse ->
                    AuthUtils.saveUserData(context, userResponse)
                    tokenVerified = true
                }?.onFailure { exception ->
                    tokenVerified = true
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    SportTrackTheme(useDarkTheme = darkTheme) {

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (showAppBar) {
                    if (isLargeAppBar) {
                        LargeTopAppBar(
                            scrollBehavior = scrollBehavior,
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            ),
                            title = {
                                Text(
                                    appBarTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                if (showBackBtn) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.Default.ChevronLeft,
                                            contentDescription = stringResource(id = R.string.menu),
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        TopAppBar(
                            scrollBehavior = scrollBehavior,
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                scrolledContainerColor = Color.Transparent
                            ),
                            title = {
                                Text(
                                    appBarTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                if (showBackBtn) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.Default.ChevronLeft,
                                            contentDescription = stringResource(id = R.string.menu),
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }
                } else {
                    Spacer(modifier = Modifier)
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = scaffoldState) { data ->
                    Snackbar(snackbarData = data)
                }
            },
            content = { paddingValues ->
                HealthConnectNavigation(
                    navController = navController,
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(top = 5.dp)
                        .padding(horizontal = 10.dp),
                    setAppBarTitle = { newTitle ->
                        appBarTitle = newTitle
                    },
                    setShouldShowBackBtn = { shouldShow ->
                        if (shouldShow != showBackBtn) {
                            showBackBtn = shouldShow
                        }
                    },
                    startDestination = startDestination,
                    setShouldShowAppBar = { shouldShow ->
                        if (shouldShow != showAppBar) {
                            showAppBar = shouldShow
                        }

                    },
                    setShouldShowBottomAppBar = { shouldShow ->
                        if (shouldShow != showBottomAppBar) {
                            showBottomAppBar = shouldShow

                        }
                    },
                    setLargeAppBar = { isLarge ->
                        isLargeAppBar = isLarge
                    },
                    exerciseViewModel = exerciseViewModel,
                    fusedLocationClient = fusedLocationClient,
                    authViewModel = authViewModel,
                    context = context,
                    isAuthenticated = isAuthenticated,
                    settingsViewModel = settingsViewModel
                )
            },
            bottomBar = {
                if (showBottomAppBar) {
                    CustomBottomNavigationBar(
                        navController = navController,
                    )
                }
            }
        )
    }
}


@Composable
fun CustomBottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val bottomNavItems = listOf(
        Screen.WelcomeScreen,
        Screen.ExerciseList,
        Screen.ProfileScreen
    )
    BottomAppBar(
        modifier = modifier,
        containerColor = BottomAppBarDefaults.containerColor.copy(0.5f)
    ) {

        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true

                        restoreState = true
                    }
                },
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = stringResource(screen.titleId),
                            Modifier.size(25.dp)
                        )
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = NavigationBarItemDefaults.colors().selectedTextColor,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = NavigationBarItemDefaults.colors().unselectedIconColor,
                    unselectedTextColor = NavigationBarItemDefaults.colors().unselectedTextColor,
                    disabledIconColor = NavigationBarItemDefaults.colors().disabledIconColor,
                    disabledTextColor = NavigationBarItemDefaults.colors().disabledTextColor
                )
            )
        }
    }
}

@Preview()
@Composable
fun CustomBottomNavigationBarPreview() {
    CustomBottomNavigationBar(rememberNavController())
}