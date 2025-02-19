package com.stapp.sporttrack.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.WelcomeActivity
import com.stapp.sporttrack.ui.components.CustomTextField
import com.stapp.sporttrack.ui.components.PasswordTextField
import com.stapp.sporttrack.ui.RegisterActivity
import com.stapp.sporttrack.ui.components.AnnotatedClickableRow
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.utils.hideKeyboard
import com.stapp.sporttrack.utils.saveUserDataAndToken
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: RegistrationViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    var isEmailValid by remember { mutableStateOf(true) }
    var hidePassword by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val isFormValid = email.isNotBlank() && isEmailValid && password.isNotBlank()

    var isLoading by remember { mutableStateOf(false) }


    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    LaunchedEffect(viewModel.loginResult) {
        viewModel.loginResult.collectLatest { result ->
            isLoading = false
            result?.onSuccess { loginResponse ->

                saveUserDataAndToken(context, loginResponse)

                Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()

                val intent = Intent(context, WelcomeActivity::class.java).apply {
                    putExtra("verifyToken", false)
                }
                context.startActivity(intent)
                (context as ComponentActivity).finish()
            }?.onFailure { exception ->
                errorMessage = exception.message.toString()
                Toast.makeText(
                    context,
                    "Erreur lors de la connexion",
                    Toast.LENGTH_SHORT
                ).show()
                println("Erreur lors de la connexion: ${exception.message}")
            }
        }
    }

    val localContext = LocalConfiguration.current
    Column(
        modifier = Modifier
            .imePadding()
            .verticalScroll(scrollState)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(
                top = localContext.screenHeightDp.dp / 8,
                start = 16.dp,
                end = 16.dp,
                bottom = 20.dp
            )
        ) {
            Text(
                text = "Connexion",
                style = TextStyle(
                    fontSize = 40.sp,
                    color = BlueBlack,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Renseignez vos informations pour vous connecter.",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = BlueBlack.copy(alpha = 0.5f),
                ),
                textAlign = TextAlign.Start,
            )
        }
        Column(
            modifier = Modifier
                .height(localContext.screenHeightDp.dp / 4)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_login),
                contentDescription = "User login image",
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
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    errorMessage = ""
                },
                leadingIcon = painterResource(id = R.drawable.baseline_mail_outline_24),
                label = { Text("Email") },
            )
            if (!isEmailValid) {
                Text(
                    text = "Email invalide",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            PasswordTextField(
                password = password,
                onPasswordChange = {
                    password = it
                    errorMessage = ""
                },
                onTrailingIconClick = { hidePassword = !hidePassword },
                hidePassword = hidePassword,
                label = "Mot de passe"
            )
            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    errorMessage = ""
                    isLoading = true
                    hideKeyboard(context)
                    viewModel.login(email, password)
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueBlack,
                    contentColor = LightGray,
                    disabledContentColor = BlueBlack.copy(alpha = 0.3f),
                    disabledContainerColor = LightGray
                )
            ) {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth(),
                    ) {
                        CircularProgressIndicator(
                            color = BlueBlack.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )

                        Text(
                            "Chargement...",
                            modifier = Modifier
                                .padding(start = 10.dp),
                        )
                    }
                } else {
                    Text(
                        "Se connecter",
                        modifier = Modifier
                            .padding(vertical = 10.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        AccountRow(context)
    }
}

@Composable
fun AccountRow(context: Context) {
    AnnotatedClickableRow(
        context = context,
        questionText = "Vous n'avez pas de compte? ",
        actionText = "Créer un compte",
        targetActivity = RegisterActivity::class.java
    ) {
        putExtra("isFromLoginActivity", true)
    }
}


