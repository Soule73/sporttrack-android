package com.stapp.sporttrack.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stapp.sporttrack.ui.components.InitViewModel
import com.stapp.sporttrack.ui.screens.auth.RegisterNavHost

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
