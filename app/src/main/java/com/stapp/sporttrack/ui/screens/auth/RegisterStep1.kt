package com.stapp.sporttrack.ui.screens.auth

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
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
import androidx.navigation.NavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.components.ClickableTextRow
import com.stapp.sporttrack.ui.components.CustomTextField
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.viewmodel.RegistrationViewModel

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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
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
            Spacer(modifier = Modifier.height(20.dp))
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
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    viewModel.email = email
                    viewModel.firstName = firstName
                    viewModel.lastName = lastName
                    navController.navigate("step2")
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
                    "Suivant",
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
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
    val annotatedString = buildAnnotatedString {
        append("Vous avez déjà un compte? ")

        pushStringAnnotation(tag = "URL", annotation = "Se connecter")
        withStyle(style = SpanStyle(color = BlueBlack, textDecoration = TextDecoration.Underline)) {
            append("Se connecter")
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
                        if (annotation.item == "Se connecter") {
                            val intent = Intent(context, LoginActivity::class.java).apply {
                                putExtra("checkAuthentication", false)
                            }
                            context.startActivity(intent)
                            (context as ComponentActivity).finish()
                        }
                    }
            }
        )
    }
}
