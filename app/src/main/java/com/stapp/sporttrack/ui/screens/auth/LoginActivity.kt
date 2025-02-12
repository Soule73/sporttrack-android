package com.stapp.sporttrack.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.components.CustomTextField
import com.stapp.sporttrack.ui.components.InitViewModel
import com.stapp.sporttrack.ui.components.PasswordTextField
import com.stapp.sporttrack.ui.screens.WelcomeActivity
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.utils.checkAuthentication
import com.stapp.sporttrack.utils.saveUserDataAndToken
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val checkAuth = intent.getBooleanExtra("checkAuthentication", true)

        if (checkAuth && checkAuthentication(this, true)) return

        enableEdgeToEdge()

        setContent {
            InitViewModel(context = this) { _, viewModel ->
                LoginScreen(viewModel)
            }
        }

    }

}

@Composable
fun LoginScreen(viewModel: RegistrationViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    var isEmailValid by remember { mutableStateOf(true) }
    var hidePassword by remember { mutableStateOf(true) }
    val isFormValid = email.isNotBlank() && isEmailValid && password.isNotBlank()

    LaunchedEffect(viewModel.loginResult) {
        viewModel.loginResult.collectLatest { result ->
            result?.onSuccess { loginResponse ->

                saveUserDataAndToken(context, loginResponse)

                Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()

                val intent = Intent(context, WelcomeActivity::class.java).apply {
                    putExtra("verifyToken", false)
                }
                context.startActivity(intent)
                (context as ComponentActivity).finish()
            }?.onFailure { exception ->

                Toast.makeText(
                    context,
                    "Erreur lors de la connexion:  ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                println("Erreur lors de la connexion: ${exception.message}")
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
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
                .height(LocalConfiguration.current.screenHeightDp.dp / 4)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_login),
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
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = Patterns.EMAIL_ADDRESS.matcher(it).matches()
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
            Spacer(modifier = Modifier.height(20.dp))
            PasswordTextField(
                password = password,
                onPasswordChange = { password = it },
                onTrailingIconClick = { hidePassword = !hidePassword },
                hidePassword = hidePassword,
                label = "Mot de passe"
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    viewModel.login(email, password)
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueBlack,
                    contentColor = LightGray,
                    disabledContentColor = BlueBlack,
                    disabledContainerColor = LightGray
                )
            ) {
                Text(
                    "Se connecter",
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        AccountRow(context)
    }
}

@Composable
fun AccountRow(context: Context) {
    val annotatedString = buildAnnotatedString {
        append("Vous n'avez pas de compte? ")

        pushStringAnnotation(tag = "URL", annotation = "Créer un compte")
        withStyle(style = SpanStyle(color = BlueBlack, textDecoration = TextDecoration.Underline)) {
            append("Créer un compte")
        }
        pop()
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),

    ) {
        BasicText(
            text = annotatedString,
            modifier = Modifier.clickable {
                annotatedString.getStringAnnotations(
                    tag = "URL",
                    start = 0,
                    end = annotatedString.length
                )
                    .firstOrNull()?.let { annotation ->
                        if (annotation.item == "Créer un compte") {
                            val intent = Intent(context, RegisterActivity::class.java).apply {
                                putExtra("isFromLoginActivity", true)
                            }
                            context.startActivity(intent)
                            (context as ComponentActivity).finish()
                        }
                    }
            }
        )
    }
}


