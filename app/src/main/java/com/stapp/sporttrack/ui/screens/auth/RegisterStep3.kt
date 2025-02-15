package com.stapp.sporttrack.ui.screens.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.components.PasswordTextField
import com.stapp.sporttrack.ui.screens.WelcomeActivity
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.utils.hideKeyboard
import com.stapp.sporttrack.utils.saveUserDataAndToken
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableInteractionSource", "UseOfNonLambdaOffsetOverload")
@Composable
fun RegisterStep3(navController: NavController, viewModel: RegistrationViewModel) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var hidePassword by remember { mutableStateOf(true) }
    var hideConfirmPassword by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var isLoading by remember { mutableStateOf(false) }

    val registrationError by viewModel.registrationError.collectAsState()

    LaunchedEffect(registrationError) {
        if (registrationError?.containsKey("error") == true) {
            Toast.makeText(context, registrationError!!["error"], Toast.LENGTH_SHORT).show()
            viewModel.clearRegistrationError()
        } else if (registrationError?.containsKey("email") == true) {
            Toast.makeText(context, registrationError!!["email"], Toast.LENGTH_SHORT).show()
            navController.navigate("step1")
        }
    }


    fun setIsLoadingFalse() {
        isLoading = false
    }

    HandleRegistrationResult(viewModel, context, setIsLoadingFalse())

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column(
        modifier = Modifier

            .imePadding()
            .verticalScroll(scrollState)
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
            isLoading = isLoading,
            onRegister = {
                isLoading = true
                hideKeyboard(context)
                viewModel.password = password
                viewModel.register()
            }
        )
    }
}

@Composable
fun HandleRegistrationResult(viewModel: RegistrationViewModel, context: Context, onRegister: Unit) {
    LaunchedEffect(viewModel.registrationResult) {

        viewModel.registrationResult.collectLatest { result ->
            onRegister.run { }
            result?.onSuccess { authResponse ->
                saveUserDataAndToken(context, authResponse)
                context.startActivity(Intent(context, WelcomeActivity::class.java).apply {
                    putExtra("verifyToken", false)
                })
                (context as ComponentActivity).finish()
                Toast.makeText(context, "Inscription réussie", Toast.LENGTH_SHORT).show()
            }?.onFailure { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()

                println("Erreur lors de l'inscription ::: ${exception.message}")
            }
        }
    }
}

@Composable
fun HeaderSection() {
    val localContext = LocalConfiguration.current

    Column(
        modifier = Modifier.padding(
            top = localContext.screenHeightDp.dp / 8,
            start = 16.dp,
            end = 16.dp,
            bottom = 20.dp
        )
    ) {
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
            contentDescription = "image field security",
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
    onRegister: () -> Unit,
    isLoading: Boolean = false
) {
    val enable = password.isNotBlank() && confirmPassword.isNotBlank() && !isLoading

    Spacer(modifier = Modifier.height(20.dp))
    Button(
        enabled = enable,
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
                "S'inscrire",
                modifier = Modifier
                    .padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }

    }
}
