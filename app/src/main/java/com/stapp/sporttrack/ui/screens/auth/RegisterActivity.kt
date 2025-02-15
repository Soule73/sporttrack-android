package com.stapp.sporttrack.ui.screens.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stapp.sporttrack.ui.components.InitViewModel
import com.stapp.sporttrack.viewmodel.RegistrationViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (checkAuthentication(this)) return

        val isFromLoginActivity = intent.getBooleanExtra("isFromLoginActivity", false)
        val startDestination = if (isFromLoginActivity) "step1" else "step0"

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.parseColor("#061428")
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.parseColor("#061428"),
            )
        )

        setContent {
            InitViewModel(context = this) { navController, viewModel ->
                RegisterNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}

@Composable
fun RegisterNavHost(
    navController: NavHostController,
    viewModel: RegistrationViewModel,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination,
        ) {
        composable("step0") { RegisterStep0(navController) }
        composable("step1") { RegisterStep1(navController, viewModel) }
        composable("step2") { RegisterStep2(navController, viewModel) }
        composable("step3") { RegisterStep3(navController,viewModel) }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegisterNavHostPreview() {
//    SportTrackTheme {
//        val navController = rememberNavController()
//        val registrationViewModel: RegistrationViewModel =
//            viewModel(factory = RegistrationViewModelFactory(AuthRepository()))
//        RegisterNavHost(
//            navController = navController, viewModel = registrationViewModel,
//            startDestination = "step0"
//        )
//    }
//}