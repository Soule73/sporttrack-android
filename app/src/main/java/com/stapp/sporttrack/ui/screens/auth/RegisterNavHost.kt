package com.stapp.sporttrack.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stapp.sporttrack.viewmodel.RegistrationViewModel

@Composable
fun RegisterNavHost(
    navController: NavHostController,
    viewModel: RegistrationViewModel,
    startDestination: String
) {
    NavHost(
        navController = navController, startDestination = startDestination,
    ) {
        composable("step0") { RegisterStep0(navController) }
        composable("step1") { RegisterStep1(navController, viewModel) }
        composable("step2") { RegisterStep2(navController, viewModel) }
        composable("step3") { RegisterStep3(navController, viewModel) }
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