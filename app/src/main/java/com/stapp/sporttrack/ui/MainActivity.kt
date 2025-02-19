package com.stapp.sporttrack.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stapp.sporttrack.data.HealthConnectManager
import com.stapp.sporttrack.ui.screens.HealthConnectApp
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import com.stapp.sporttrack.viewmodel.RegistrationViewModelFactory

class WelcomeActivity : ComponentActivity() {

    private val healthConnectManager: HealthConnectManager by lazy {
        HealthConnectManager(this)
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val verifyToken = intent.getBooleanExtra("verifyToken", true)
        enableEdgeToEdge()

        setContent {
            val viewModelStoreOwner = LocalViewModelStoreOwner.current
            val sharedPreferences =
                this.getSharedPreferences(
                    SharedPreferencesConstants.PREF_NAME,
                    Context.MODE_PRIVATE
                )
            viewModelStoreOwner?.let {
                val registrationViewModel: RegistrationViewModel = viewModel(
                    it,
                    factory = RegistrationViewModelFactory(sharedPreferences)
                )
                HealthConnectApp(
                    context = this,
                    healthConnectManager = healthConnectManager,
                    registrationViewModel = registrationViewModel,
                    verifyToken = verifyToken
                )
            }
        }
    }
}