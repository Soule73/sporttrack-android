package com.stapp.sporttrack.ui.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import com.stapp.sporttrack.ui.screens.auth.LoginScreen
import com.stapp.sporttrack.ui.screens.auth.RegisterStep0
import com.stapp.sporttrack.ui.screens.auth.RegisterStep1
import com.stapp.sporttrack.ui.screens.auth.RegisterStep2
import com.stapp.sporttrack.ui.screens.auth.RegisterStep3
import com.stapp.sporttrack.ui.screens.exercise.ExerciseListScreen
import com.stapp.sporttrack.ui.screens.exercise.ExerciseSessionDetailScreen
import com.stapp.sporttrack.ui.screens.exercise.ExerciseSessionScreen
import com.stapp.sporttrack.ui.screens.privacypolicy.PrivacyPolicyScreen
import com.stapp.sporttrack.ui.screens.profil.ProfileScreen
import com.stapp.sporttrack.ui.screens.welcome.DailyDetailsScreen
import com.stapp.sporttrack.ui.screens.welcome.TrainingDetailsScreen
import com.stapp.sporttrack.ui.screens.welcome.WelcomeScreen
import com.stapp.sporttrack.ui.theme.SettingsViewModel
import com.stapp.sporttrack.utils.AuthUtils
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.viewmodel.AuthViewModel
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


/**
 * Provides the navigation in the app.
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun HealthConnectNavigation(
    navController: NavHostController,
    modifier: Modifier,
    setAppBarTitle: (String) -> Unit,
    setShouldShowBackBtn: (Boolean) -> Unit,
    startDestination: String = Screen.WelcomeScreen.route,
    setShouldShowAppBar: (Boolean) -> Unit,
    setShouldShowBottomAppBar: (Boolean) -> Unit,
    setLargeAppBar: (Boolean) -> Unit,
    exerciseViewModel: ExerciseViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    authViewModel: AuthViewModel,
    context: Context,
    isAuthenticated: Boolean,
    settingsViewModel: SettingsViewModel,
    darkTheme: Boolean,
) {

    NavHost(navController = navController, startDestination = startDestination) {
        @Composable
        fun setupScreen(screen: Screen, titleId: String? = null) {
            setShouldShowAppBar(screen.hasAppBar)
            setLargeAppBar(screen.isLargeAppBar)
            setShouldShowBackBtn(screen.hasBackBtn)
            setShouldShowBottomAppBar(screen.hasBottomAppBar)

            if (titleId != null) {
                setAppBarTitle(titleId)
            } else {
                setAppBarTitle(stringResource(screen.titleId))
            }

        }

        composable(Screen.LoginScreen.route) {
            setupScreen(Screen.LoginScreen)

            LoginScreen(
                context = context,
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Screen.RegisterScreen.route) {
            setupScreen(Screen.RegisterScreen)

            RegisterStep0(
                navController = navController
            )
        }
        composable(Screen.RegisterScreenStep1.route) {
            setupScreen(Screen.RegisterScreenStep1)
            RegisterStep1(
                navController = navController,
                viewModel = authViewModel,
                isAuthenticated = isAuthenticated
            )
        }
        composable(Screen.RegisterScreenStep2.route) {
            setupScreen(Screen.RegisterScreenStep2)

            RegisterStep2(
                context = context,
                navController = navController,
                viewModel = authViewModel,
                isAuthenticated = isAuthenticated
            )
        }
        composable(Screen.RegisterScreenStep3.route) {
            setShouldShowAppBar(Screen.RegisterScreenStep3.hasAppBar)
            setShouldShowBackBtn(Screen.RegisterScreenStep3.hasBackBtn)
            setShouldShowBottomAppBar(Screen.RegisterScreenStep3.hasBottomAppBar)
            setLargeAppBar(Screen.RegisterScreenStep3.isLargeAppBar)
            RegisterStep3(
                context = context,
                navController = navController,
                viewModel = authViewModel,
                isAuthenticated = isAuthenticated
            )
        }
        composable(
            "${Screen.DailyDetailsScreen.route}/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: return@composable
            val date = LocalDate.parse(dateString)

            val title = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRANCE))
            setupScreen(Screen.DailyDetailsScreen, title)
            DailyDetailsScreen(
                navController = navController,
                date = date,
                exerciseViewModel = exerciseViewModel,
                modifier = modifier
            )
        }
        composable(Screen.TrainingDetailsScreen.route) {
            setupScreen(Screen.TrainingDetailsScreen)
            TrainingDetailsScreen(
                modifier = modifier,
                navController = navController,
                exerciseViewModel = exerciseViewModel
            )
        }
        composable(Screen.WelcomeScreen.route) {
            setupScreen(Screen.WelcomeScreen)

            WelcomeScreen(
                modifier = modifier,
                navController = navController,
                exerciseViewModel = exerciseViewModel
            )
        }
        composable(Screen.PrivacyPolicy.route) {
            setupScreen(Screen.PrivacyPolicy)
            PrivacyPolicyScreen(modifier = modifier)
        }
        composable(Screen.ExerciseList.route) {
            setupScreen(Screen.ExerciseList)

            ExerciseListScreen(
                context = context,
                navController = navController,
                modifier = modifier
            )
        }
        composable("${Screen.ExerciseSessionDetail.route}/{destination}",arguments = listOf(navArgument("destination") { type = NavType.StringType })) {backStackEntry ->
            val destination = backStackEntry.arguments?.getString("destination")
            setupScreen(Screen.ExerciseSessionDetail)
            ExerciseSessionDetailScreen(
                modifier = modifier,
                onBackClick = {
                    if (!destination.isNullOrEmpty() && destination == Screen.ExerciseList.route) {
                        navController.navigate(Screen.ExerciseList.route)
                    } else {
                        navController.popBackStack()
                    }
                },
                exerciseViewModel = exerciseViewModel,
                context = context

            )
        }
        composable(Screen.ProfileScreen.route) {

            setupScreen(Screen.ProfileScreen,"")
            AuthUtils.getUserData(context)?.let { it1 -> authViewModel.setUserData(it1) }
            ProfileScreen(
                context = context,
                navController = navController,
                authViewModel = authViewModel,
                modifier = modifier,
                settingsViewModel = settingsViewModel
            )
        }
        composable(
            route = Screen.ExerciseSession.route + "/{$EXERCISE_TYPE_NAV_ARGUMENT}",
            arguments = listOf(navArgument(EXERCISE_TYPE_NAV_ARGUMENT) { type = NavType.IntType })
        ) { backStackEntry ->
            val exerciseType = backStackEntry.arguments?.getInt(EXERCISE_TYPE_NAV_ARGUMENT) ?: -1

            val titleId = ExerciseUtils.getExerciseStrategy(exerciseType).getTitle()

            setupScreen(Screen.ExerciseSession, stringResource(titleId))

            ExerciseSessionScreen(
                context = context,
                authViewModel = authViewModel,
                exerciseType = exerciseType,
                exerciseViewModel = exerciseViewModel,
                fusedLocationClient = fusedLocationClient,
                modifier = modifier,
                darkTheme = darkTheme,
                onClickFinished = {
                    navController.navigate("${Screen.ExerciseSessionDetail.route}/${Screen.ExerciseList.route}")
                }
            )
        }
    }
}
