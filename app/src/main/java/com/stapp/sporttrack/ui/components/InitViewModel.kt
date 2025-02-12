package com.stapp.sporttrack.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import com.stapp.sporttrack.viewmodel.RegistrationViewModelFactory

@Composable
fun InitViewModel(
    context: Context,
    content: @Composable (NavHostController, RegistrationViewModel) -> Unit
) {
    SportTrackTheme {
        val navController = rememberNavController()
        val viewModelStoreOwner = LocalViewModelStoreOwner.current
        val sharedPreferences =
            context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
        viewModelStoreOwner?.let {
            val registrationViewModel: RegistrationViewModel = viewModel(
                it,
                factory = RegistrationViewModelFactory(sharedPreferences)
            )
            content(navController, registrationViewModel)
        }
    }
}