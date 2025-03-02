package com.stapp.sporttrack.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stapp.sporttrack.data.repository.ExerciseRepository
import com.stapp.sporttrack.ui.navigation.EXERCISE_TYPE_NAV_ARGUMENT
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.screens.HealthConnectApp
import com.stapp.sporttrack.ui.theme.SettingsViewModel
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import com.stapp.sporttrack.viewmodel.AuthViewModel
import com.stapp.sporttrack.viewmodel.AuthViewModelFactory
import com.stapp.sporttrack.viewmodel.ExerciseViewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var exerciseRepository: ExerciseRepository

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

            val sharedPreferences =
                this.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        settingsViewModel = SettingsViewModel(this)
        exerciseRepository = ExerciseRepository(sharedPreferences)

        setContent {
            val navController = rememberNavController()
            var verifyToken = true
            val viewModelStoreOwner = LocalViewModelStoreOwner.current

            val themePreference by settingsViewModel.themePreference.collectAsState()

            val darkTheme = when (themePreference) {
                SharedPreferencesConstants.PREF_LIGHT_MODE -> false
                SharedPreferencesConstants.PREF_DARK_MODE -> true
                else -> isSystemInDarkTheme()
            }

            viewModelStoreOwner?.let {
                val authViewModel: AuthViewModel =
                    viewModel(it, factory = AuthViewModelFactory(this, sharedPreferences))

                val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
                val isFirstOpen by authViewModel.isFirstOpen.collectAsState()

                val startDestination = if (isAuthenticated) {
                    Screen.WelcomeScreen.route
                } else if (isFirstOpen) {
                    verifyToken = false
                    Screen.RegisterScreen.route
                } else {
                    verifyToken = false
                    Screen.LoginScreen.route
                }

                val exerciseViewModel: ExerciseViewModel = remember {
                    ViewModelProvider(
                        viewModelStoreOwner,
                        ExerciseViewModelFactory(exerciseRepository)
                    )[ExerciseViewModel::class.java].apply {
                        initializeFromSharedState()
                    }
                }

                HealthConnectApp(
                    context = this,
                    verifyToken = verifyToken,
                    authViewModel = authViewModel,
                    fusedLocationClient = fusedLocationClient,
                    exerciseViewModel = exerciseViewModel,
                    navController = navController,
                    mainViewModel = mainViewModel,
                    startDestination = startDestination,
                    isAuthenticated=isAuthenticated,
                    settingsViewModel = settingsViewModel,
                    darkTheme=darkTheme
                )
            }
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.getIntExtra(EXERCISE_TYPE_NAV_ARGUMENT, -1)?.takeIf { it != -1 }?.let { exerciseType ->

            val destination = "${Screen.ExerciseSession.route}/$exerciseType"
            mainViewModel.navigateTo(destination)
        }
    }
}


class MainViewModel : ViewModel() {
    private val _navigationCommands = Channel<String?>(Channel.BUFFERED)
    val navigationCommands = _navigationCommands.receiveAsFlow()

    fun navigateTo(destination: String) {
        viewModelScope.launch {
            _navigationCommands.send(destination)
        }
    }
}