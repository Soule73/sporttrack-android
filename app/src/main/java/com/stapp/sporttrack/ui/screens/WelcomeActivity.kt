package com.stapp.sporttrack.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.ui.components.InitViewModel
import com.stapp.sporttrack.ui.screens.auth.LoginActivity
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.utils.getUserData
import com.stapp.sporttrack.utils.saveUserData
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collectLatest

class WelcomeActivity : ComponentActivity() {

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val verifyToken = intent.getBooleanExtra("verifyToken", true)

        getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
        setContent {
            InitViewModel(context = this) { _, viewModel ->
                MainScreen(viewModel, verifyToken)
            }
        }
    }

}

@Composable
fun MainScreen(
    viewModel: RegistrationViewModel,
    verifyToken: Boolean = true
) {
    val context = LocalContext.current
    val userData = getUserData(LocalContext.current)

    var tokenVerified by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        if (verifyToken && !tokenVerified) {
            viewModel.verifyToken()
            viewModel.verifyTokenResult.collectLatest { result ->
                result?.onSuccess { userResponse ->

                    saveUserData(context, userResponse)
                    tokenVerified = true
                }?.onFailure { exception ->

                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as ComponentActivity).finish()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue à SportTrack ${userData?.firstName} !",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.logout(context)

                val intent = Intent(context, LoginActivity::class.java).apply {
                    putExtra("checkAuthentication", false)
                }
                context.startActivity(intent)
                (context as Activity).finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueBlack,
                contentColor = LightGray
            )
        ) {
            Text(
                "Se déconnecter",
                modifier = Modifier
                    .padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }
    }
}