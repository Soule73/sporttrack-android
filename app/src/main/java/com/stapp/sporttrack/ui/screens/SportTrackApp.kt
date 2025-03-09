package com.stapp.sporttrack.ui.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
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

    LaunchedEffect(mainViewModel) {
        mainViewModel.navigationCommands.collect { destination ->
            destination?.let { navController.navigate(it) }
        }
    }

    LaunchedEffect(verifyToken, verificationAttempted) {
        if (verifyToken && !verificationAttempted) {
            verificationAttempted = true
            authViewModel.verifyToken()
            authViewModel.verifyTokenResult.collectLatest { result ->
                result?.onSuccess { userResponse ->
                    AuthUtils.saveUserData(context, userResponse)
                    tokenVerified = true
                }?.onFailure { _ ->
                    tokenVerified = true
                }
            }
        }
    }

    SportTrackTheme(useDarkTheme = darkTheme) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (showAppBar) {
                    HealthConnectTopAppBar(
                        title = appBarTitle,
                        showBackButton = showBackBtn,
                        isLargeAppBar = isLargeAppBar,
                        onBackButtonClick = { navController.popBackStack() },
                        scrollBehavior = scrollBehavior
                    )
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
                        .padding(horizontal = 10.dp),
                    setAppBarTitle = { appBarTitle = it },
                    setShouldShowBackBtn = { showBackBtn = it },
                    startDestination = startDestination,
                    setShouldShowAppBar = { showAppBar = it },
                    setShouldShowBottomAppBar = { showBottomAppBar = it },
                    setLargeAppBar = { isLargeAppBar = it },
                    exerciseViewModel = exerciseViewModel,
                    fusedLocationClient = fusedLocationClient,
                    authViewModel = authViewModel,
                    context = context,
                    isAuthenticated = isAuthenticated,
                    settingsViewModel = settingsViewModel,
                    darkTheme = darkTheme
                )
            },
            bottomBar = {
                if (showBottomAppBar) {
//                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 10.dp)
//                            .padding(bottom =  10.dp).fillMaxWidth()
//                    ) {
                    CustomBottomNavigationBar(navController = navController)
//                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectTopAppBar(
    title: String,
    showBackButton: Boolean,
    isLargeAppBar: Boolean,
    onBackButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val appBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    )

    if (isLargeAppBar) {
        LargeTopAppBar(
            title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackButtonClick) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = stringResource(id = R.string.menu),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = appBarColors
        )
    } else {
        TopAppBar(
            title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackButtonClick) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = stringResource(id = R.string.menu),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            colors = appBarColors
        )
    }
}

fun Modifier.topBorder(
    color: Color,
    height: Float,
) = this.drawWithContent {
    drawContent()
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = height,
    )
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
        Screen.ExerciseList,Screen.ProfileScreen
    )

    BottomAppBar(
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 0.dp,
        modifier = modifier

            .topBorder(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f), 1f)
            .clip(MaterialTheme.shapes.extraLarge),

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
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    screen.icon?.let {
                        val iconModifier = Modifier.size(if (screen == Screen.ExerciseList) 50.dp else 35.dp)
                        if (screen == Screen.ExerciseList) {
                            val backgroundModifier = if (isSelected) {
                                Modifier.background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            } else {
                                Modifier.background(MaterialTheme.colorScheme.onSurface, CircleShape)
                            }
                            Icon(
                                imageVector = it,
                                contentDescription = stringResource(screen.titleId),
                                modifier = iconModifier.then(backgroundModifier).padding(10.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.surface


                            )
                        } else {
                            Icon(
                                imageVector = it,
                                contentDescription = stringResource(screen.titleId),
                                Modifier.size(35.dp)
                            )
                        }
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

@Preview
@Composable
fun CustomBottomNavigationBarPreview() {
    CustomBottomNavigationBar(rememberNavController())
}
