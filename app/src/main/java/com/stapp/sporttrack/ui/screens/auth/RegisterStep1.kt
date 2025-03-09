package com.stapp.sporttrack.ui.screens.auth

import android.content.Context
import android.content.res.Configuration
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.repository.AuthRepository
import com.stapp.sporttrack.ui.components.AnnotatedClickableRow
import com.stapp.sporttrack.ui.components.AppTextInputColors
import com.stapp.sporttrack.ui.components.CustomTextField
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.utils.convertMillisToDate
import com.stapp.sporttrack.utils.convertMillisToDateFr
import com.stapp.sporttrack.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterStep1(
    navController: NavController,
    viewModel: AuthViewModel,
    isAuthenticated: Boolean = false
) {
    val email by viewModel.email.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

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
        if(isAuthenticated){
            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(bottom = 20.dp)

            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(top = 50.dp, start = 16.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(
                top = localContext.screenHeightDp.dp / 8,
                start = 16.dp,
                end = 16.dp,
                bottom = 20.dp
            )
        ) {
            Text(
                text = if (isAuthenticated) "Modifier vos informations personel" else "Créer un compte",
                style = TextStyle(
                    fontSize = 35.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Start,
            )
            Text(
                text = if (isAuthenticated) "" else "Vos données sportives un seul endroit.",
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                textAlign = TextAlign.Start,
            )
        }
        if (!isAuthenticated) {
            Column(
                modifier =
                Modifier
                    .height(configuration.screenHeightDp.dp / 4)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.activity_tracker),
                    contentDescription = "Activity tracker Image",
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    Color.Transparent
                                )
                            )
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
            }
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
                    viewModel.setEmail(it)
                    isEmailValid = Patterns.EMAIL_ADDRESS.matcher(it).matches()
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
                    viewModel.setFirstName(it)
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
                    viewModel.setLastName(it)
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
            Spacer(modifier = Modifier.height(10.dp))
            if (isAuthenticated) {
                DatePickerFieldToModal(
                    selectedDate = selectedDate,
                    onDateSelected = {
                        val date = it?.let { it1 -> convertMillisToDate(it1) }
                        viewModel.setDateOfBirth(date)
                        viewModel.setSelectedDate(it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {

                    viewModel.clearRegistrationError()
                    navController.navigate(Screen.RegisterScreenStep2.route)
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
            ) {
                Text(
                    "Suivant",
                    modifier = Modifier.padding(vertical = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }

            if (!isAuthenticated) {
                LoginRow(onClick = { navController.navigate(Screen.LoginScreen.route) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerFieldToModal(
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    selectedDate: Long? = null,
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        colors = AppTextInputColors,
        value = selectedDate?.let { convertMillisToDateFr(it) } ?: "",
        onValueChange = { },
        label = { Text("Date de naissance") },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Selectionner la date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerDialog(
            onDismissRequest = { showModal = false },
            confirmButton = { Text(text = "ok") },

            content = {
                DatePickerModalInput(
                    onDateSelected = { onDateSelected(it) },
                    onDismiss = { showModal = false }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark",
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight",
    showBackground = true
)
@Composable
fun RegisterStep1Preview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = AuthViewModel(

        authRepository = AuthRepository(
            sharedPreferences = context.getSharedPreferences(
                SharedPreferencesConstants.PREF_NAME,
                Context.MODE_PRIVATE
            )
        ),
        context = context
    )
    SportTrackTheme {
        RegisterStep1(navController, viewModel)
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark",
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight",
    showBackground = true
)
@Composable
fun UpdateProfilePreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = AuthViewModel(

        authRepository = AuthRepository(
            sharedPreferences = context.getSharedPreferences(
                SharedPreferencesConstants.PREF_NAME,
                Context.MODE_PRIVATE
            )
        ),
        context = context
    )
    SportTrackTheme {
        RegisterStep1(navController, viewModel, true)
    }
}

@Composable
fun LoginRow(onClick: () -> Unit) {
    AnnotatedClickableRow(
        questionText = "Vous avez déjà un compte? ",
        actionText = "Se connecter",
        onClick = { onClick() }
//        targetActivity = LoginActivity::class.java
    )
}