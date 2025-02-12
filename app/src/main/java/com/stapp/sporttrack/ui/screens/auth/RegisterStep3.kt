package com.stapp.sporttrack.ui.screens.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.components.PasswordTextField
import com.stapp.sporttrack.ui.screens.WelcomeActivity
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.utils.saveUserDataAndToken
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedMutableInteractionSource", "UseOfNonLambdaOffsetOverload")
@Composable
fun RegisterStep3(viewModel: RegistrationViewModel) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var hidePassword by remember { mutableStateOf(true) }
    var hideConfirmPassword by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    HandleRegistrationResult(viewModel, context)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        HeaderSection()
        SecurityImageSection(configuration)
        PasswordFieldsSection(
            password = password,
            confirmPassword = confirmPassword,
            hidePassword = hidePassword,
            hideConfirmPassword = hideConfirmPassword,
            onPasswordChange = { password = it },
            onConfirmPasswordChange = { confirmPassword = it },
            onPasswordVisibilityChange = { hidePassword = !hidePassword },
            onConfirmPasswordVisibilityChange = { hideConfirmPassword = !hideConfirmPassword }
        )
        RegisterButton(
            password = password,
            confirmPassword = confirmPassword,
            context = context,
            onRegister = {
                viewModel.password = password
                viewModel.register() }
        )
    }
}

@Composable
fun HandleRegistrationResult(viewModel: RegistrationViewModel, context: Context) {
    LaunchedEffect(viewModel.registrationResult) {
        viewModel.registrationResult.collectLatest { result ->
            result?.onSuccess { authResponse ->
                saveUserDataAndToken(context, authResponse)
                context.startActivity(Intent(context, WelcomeActivity::class.java).apply {
                    putExtra("verifyToken", false)
                })
                (context as ComponentActivity).finish()
                Toast.makeText(context, "Inscription réussie", Toast.LENGTH_SHORT).show()
            }?.onFailure { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()

                println("Erreur lors de l'inscription : ${exception.message}")
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
        Text(
            text = "Créer un mot de passe",
            style = TextStyle(
                fontSize = 40.sp,
                color = BlueBlack,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Choisir un mot de passe fort et sécurisé.",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = BlueBlack.copy(alpha = 0.5f),
            ),
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
fun SecurityImageSection(configuration: Configuration) {
    Column(
        modifier = Modifier
            .height(configuration.screenHeightDp.dp / 4)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.icons_security),
            contentDescription = "Welcome Image",
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            LightGray,
                            Color.Transparent
                        )
                    )
                )
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )
    }
}

@Composable
fun PasswordFieldsSection(
    password: String,
    confirmPassword: String,
    hidePassword: Boolean,
    hideConfirmPassword: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onConfirmPasswordVisibilityChange: () -> Unit
) {
    Column(modifier = Modifier.padding(20.dp)) {
        PasswordTextField(
            password = password,
            onPasswordChange = onPasswordChange,
            onTrailingIconClick = onPasswordVisibilityChange,
            hidePassword = hidePassword,
            label = "Mot de passe"
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextField(
            password = confirmPassword,
            onPasswordChange = onConfirmPasswordChange,
            onTrailingIconClick = onConfirmPasswordVisibilityChange,
            hidePassword = hideConfirmPassword,
            label = "Confirmer le mot de passe"
        )
    }
}

@Composable
fun RegisterButton(
    password: String,
    confirmPassword: String,
    context: Context,
    onRegister: () -> Unit
) {
    Spacer(modifier = Modifier.height(40.dp))
    Button(
        onClick = {
            when {
                password != confirmPassword -> {
                    Toast.makeText(
                        context,
                        "Les mots de passe ne correspondent pas",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                password.length < 8 -> {
                    Toast.makeText(
                        context,
                        "Le mot de passe doit contenir au moins 8 caractères",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> onRegister()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BlueBlack,
            contentColor = LightGray,
            disabledContentColor = BlueBlack,
            disabledContainerColor = LightGray
        )
    ) {
        Text(
            "S'inscrire",
            modifier = Modifier
                .padding(vertical = 10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
}
