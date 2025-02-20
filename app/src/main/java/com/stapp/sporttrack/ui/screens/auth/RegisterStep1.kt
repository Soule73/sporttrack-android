package com.stapp.sporttrack.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.stapp.sporttrack.ui.LoginActivity
import com.stapp.sporttrack.ui.components.AnnotatedClickableRow
import com.stapp.sporttrack.ui.components.CustomTextField
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.viewmodel.RegistrationViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterStep1(navController: NavController, viewModel: RegistrationViewModel) {
    var email by remember { mutableStateOf(viewModel.email) }
    var firstName by remember { mutableStateOf(viewModel.firstName) }
    var lastName by remember { mutableStateOf(viewModel.lastName) }

    var isEmailValid by remember { mutableStateOf(true) }
    var isFirstNameValid by remember { mutableStateOf(true) }
    var isLastNameValid by remember { mutableStateOf(true) }

    val isFormValid = email.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank() &&
            isEmailValid && isFirstNameValid && isLastNameValid

    val configuration = LocalConfiguration.current

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    val localContext = LocalConfiguration.current

    val registrationError by viewModel.registrationError.collectAsState()

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
        Column(
            modifier = Modifier.padding(
                top = localContext.screenHeightDp.dp / 8,
                start = 16.dp,
                end = 16.dp,
                bottom = 20.dp
            )
        ) {
            Text(
                text = "Créer un compte",
                style = TextStyle(
                    fontSize = 40.sp,
                    color = BlueBlack,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Start,
            )
            Text(
                text = "Vos données sportives un seul endroit.",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = BlueBlack.copy(alpha = 0.5f),
                ),
                textAlign = TextAlign.Start,
            )
        }
        Column(
            modifier =
            Modifier
                .height(configuration.screenHeightDp.dp / 4)
                .fillMaxWidth()
        ) {

            Image(
                painter = painterResource(id = R.drawable.activity_tracker),
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
            modifier = Modifier
                .padding(20.dp)
                .height(configuration.screenHeightDp.dp / 2)
        ) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    viewModel.clearRegistrationError()
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
            if (registrationError?.containsKey("email") == true) {
                Text(
                    text = registrationError!!["email"]!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = firstName,
                onValueChange = {
                    firstName = it
                    isFirstNameValid = it.length >= 3
                },
                leadingIcon = painterResource(id = R.drawable.baseline_person_24),
                label = { Text("Prénom") },
            )
            if (!isFirstNameValid) {
                Text(
                    text = "Le prénom doit contenir au moins 3 caractères",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = lastName,
                onValueChange = {
                    lastName = it
                    isLastNameValid = it.length >= 3
                },
                leadingIcon = painterResource(id = R.drawable.baseline_person_24),
                label = { Text("Nom") },
            )
            if (!isLastNameValid) {
                Text(
                    text = "Le nom doit contenir au moins 3 caractères",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.email = email
                    viewModel.firstName = firstName
                    viewModel.lastName = lastName
                    viewModel.clearRegistrationError()
                    navController.navigate("step2")
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueBlack,
                    contentColor = LightGray,
                    disabledContentColor = BlueBlack.copy(alpha = 0.5f),
                    disabledContainerColor = LightGray
                )
            ) {
                Text(
                    "Suivant",
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
            LoginRow(context = LocalContext.current)
        }
    }
}

//@Composable
//fun LoginRow(context: Context) {
//    ClickableTextRow(
//        context = context,
//        questionText = "Vous avez déjà un compte? ",
//        actionText = "Se connecter",
//        targetActivity = LoginActivity::class.java
//    )
//}

@Composable
fun LoginRow(context: Context) {
    AnnotatedClickableRow(
        context = context,
        questionText = "Vous avez déjà un compte? ",
        actionText = "Se connecter",
        targetActivity = LoginActivity::class.java
    ) {
        putExtra("checkAuthentication", false)
    }
}