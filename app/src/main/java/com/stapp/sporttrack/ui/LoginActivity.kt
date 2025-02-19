package com.stapp.sporttrack.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stapp.sporttrack.ui.components.InitViewModel
import com.stapp.sporttrack.ui.screens.auth.LoginScreen
import com.stapp.sporttrack.utils.checkAuthentication

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val checkAuth = intent.getBooleanExtra("checkAuthentication", true)

        if (checkAuth && checkAuthentication(this, true)) return

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.parseColor("#061428"),
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.parseColor("#061428"),
            )
        )

        setContent {
            InitViewModel(context = this) { _, viewModel ->
                LoginScreen(viewModel)
            }
        }

    }

}




