package com.stapp.sporttrack.ui.screens.auth

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.repository.AuthRepository
import com.stapp.sporttrack.ui.components.PasswordTextField
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.AuthUtils
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.utils.hideKeyboard
import com.stapp.sporttrack.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun RegisterStep3(
    navController: NavController,
    viewModel: AuthViewModel,
    context: Context,
    isAuthenticated: Boolean = false
) {
    val password by viewModel.password.collectAsState()

    var currentPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var hidePassword by remember { mutableStateOf(true) }
    var hideCurrentPassword by remember { mutableStateOf(true) }
    var hideConfirmPassword by remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current

    var isLoading by remember { mutableStateOf(false) }

    val registrationError by viewModel.registrationError.collectAsStateWithLifecycle()

    fun setIsLoadingFalse() {
        isLoading = false
    }
    LaunchedEffect(registrationError) {
        if (registrationError?.containsKey("error") == true) {
            Toast.makeText(context, registrationError!!["error"], Toast.LENGTH_SHORT).show()
            viewModel.clearRegistrationError()
            setIsLoadingFalse()
        } else if (registrationError?.containsKey("email") == true) {
            Toast.makeText(context, registrationError!!["email"], Toast.LENGTH_SHORT).show()
            navController.navigate("step1")
        }
    }


    HandleRegistrationResult(viewModel, context, setIsLoadingFalse(), onSuccess = {
        navController.navigate(
            Screen.WelcomeScreen.route
        )
    })

    val changePasswordResult by viewModel.changePasswordResult.collectAsStateWithLifecycle()

    LaunchedEffect(changePasswordResult) {
        changePasswordResult?.let { result ->
            result.onSuccess {
                Toast.makeText(context, "Mot de passe modifié avec succès !", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.ProfileScreen.route)
                setIsLoadingFalse()
            }.onFailure {exception->
                print("erreur lors de la modification du mot de passe : ${exception.message}")
                Toast.makeText(context, "Erreur lors de la modification du mot de passe", Toast.LENGTH_SHORT).show()
                setIsLoadingFalse()
            }
            viewModel.resetChangePasswordResult()
        }
    }

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
        HeaderSection(isAuthenticated)
        if(!isAuthenticated) {
        SecurityImageSection(configuration)
        }
        if (isAuthenticated) {
            PasswordFieldsSection(
                password = password,
                isAuthenticated=true,
                confirmPassword = confirmPassword,
                hidePassword = hidePassword,
                hideConfirmPassword = hideConfirmPassword,
                currentPassword = currentPassword,
                hideCurrentPassword = hideCurrentPassword,
                onPasswordChange = { viewModel.setPassword(it) },
                onConfirmPasswordChange = { confirmPassword = it },
                onCurrentPasswordChange = { currentPassword = it },
                onPasswordVisibilityChange = { hidePassword = !hidePassword },
                onConfirmPasswordVisibilityChange = { hideConfirmPassword = !hideConfirmPassword },
                onCurrentPasswordVisibilityChange = { hideCurrentPassword = !hideCurrentPassword }
            )
            UpdatePasswordButton(
                currentPassword = currentPassword,
                password = password,
                confirmPassword = confirmPassword,
                context = context,
                isLoading = isLoading,
                onUpdatePassword = {
                    isLoading = true
                    hideKeyboard(context)

                    viewModel.updatePassword(currentPassword, password)
                }
            )
        } else {
            PasswordFieldsSection(
                password = password,
                isAuthenticated=false,
                confirmPassword = confirmPassword,
                hidePassword = hidePassword,
                hideConfirmPassword = hideConfirmPassword,
                onPasswordChange = { viewModel.setPassword(it) },
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

                    viewModel.setPassword(password)
                    viewModel.register()
                }
            )
        }
    }
}

@Composable
fun HeaderSection(isAuthenticated: Boolean) {
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
            text = if (isAuthenticated) "Modifier le mot de passe" else "Créer un mot de passe",
            style = TextStyle(
                fontSize = 40.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (isAuthenticated) "Entrez votre mot de passe actuel et choisissez un nouveau mot de passe." else "Choisir un mot de passe fort et sécurisé.",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            textAlign = TextAlign.Start,
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
    onConfirmPasswordVisibilityChange: () -> Unit,
    currentPassword: String = "",
    hideCurrentPassword: Boolean = true,
    onCurrentPasswordChange: (String) -> Unit = {},
    onCurrentPasswordVisibilityChange: () -> Unit = {},
    isAuthenticated: Boolean
) {
    Column(modifier = Modifier.padding(20.dp)) {
        if (isAuthenticated) {
            PasswordTextField(
                password = currentPassword,
                onPasswordChange = onCurrentPasswordChange,
                onTrailingIconClick = onCurrentPasswordVisibilityChange,
                hidePassword = hideCurrentPassword,
                label = "Mot de passe actuel"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        PasswordTextField(
            password = password,
            onPasswordChange = onPasswordChange,
            onTrailingIconClick = onPasswordVisibilityChange,
            hidePassword = hidePassword,
            label = "Nouveau mot de passe"
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextField(
            password = confirmPassword,
            onPasswordChange = onConfirmPasswordChange,
            onTrailingIconClick = onConfirmPasswordVisibilityChange,
            hidePassword = hideConfirmPassword,
            label = "Confirmer mot de passe"
        )
    }
}

@Composable
fun UpdatePasswordButton(
    currentPassword: String,
    password: String,
    confirmPassword: String,
    context: Context,
    onUpdatePassword: () -> Unit,
    isLoading: Boolean = false
) {
    val enable = currentPassword.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && !isLoading

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

                else -> onUpdatePassword()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
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
                    color = MaterialTheme.colorScheme.primary,
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
                "Modifier le mot de passe",
                modifier = Modifier
                    .padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }
    }
}


//@SuppressLint("UnrememberedMutableInteractionSource", "UseOfNonLambdaOffsetOverload")
//@Composable
//fun RegisterStep3(
//    navController: NavController,
//    viewModel: AuthViewModel,
//    context: Context,
//    isAuthenticated: Boolean = false
//) {
//
//    val password by viewModel.password.collectAsState()
//
//    var confirmPassword by remember { mutableStateOf("") }
//    var hidePassword by remember { mutableStateOf(true) }
//    var hideConfirmPassword by remember { mutableStateOf(true) }
//    val configuration = LocalConfiguration.current
//
//    var isLoading by remember { mutableStateOf(false) }
//
//    val registrationError by viewModel.registrationError.collectAsStateWithLifecycle()
//
//    LaunchedEffect(registrationError) {
//        if (registrationError?.containsKey("error") == true) {
//            Toast.makeText(context, registrationError!!["error"], Toast.LENGTH_SHORT).show()
//            viewModel.clearRegistrationError()
//        } else if (registrationError?.containsKey("email") == true) {
//            Toast.makeText(context, registrationError!!["email"], Toast.LENGTH_SHORT).show()
//            navController.navigate("step1")
//        }
//    }
//
//    fun setIsLoadingFalse() {
//        isLoading = false
//    }
//
//    HandleRegistrationResult(viewModel, context, setIsLoadingFalse(), onSuccess = {
//        navController.navigate(
//            Screen.WelcomeScreen.route
//        )
//    })
//
//    val scrollState = rememberScrollState()
//    val coroutineScope = rememberCoroutineScope()
//    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
//
//    LaunchedEffect(key1 = keyboardHeight) {
//        coroutineScope.launch {
//            scrollState.scrollBy(keyboardHeight.toFloat())
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .imePadding()
//            .verticalScroll(scrollState)
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.Center
//    ) {
//        HeaderSection()
//        SecurityImageSection(configuration)
//        PasswordFieldsSection(
//            password = password,
//            confirmPassword = confirmPassword,
//            hidePassword = hidePassword,
//            hideConfirmPassword = hideConfirmPassword,
//            onPasswordChange = { viewModel.setPassword(it) },
//            onConfirmPasswordChange = { confirmPassword = it },
//            onPasswordVisibilityChange = { hidePassword = !hidePassword },
//            onConfirmPasswordVisibilityChange = { hideConfirmPassword = !hideConfirmPassword }
//        )
//        RegisterButton(
//            password = password,
//            confirmPassword = confirmPassword,
//            context = context,
//            isLoading = isLoading,
//            onRegister = {
//                isLoading = true
//                hideKeyboard(context)
//
//                viewModel.setPassword(password)
//                viewModel.register()
//            }
//        )
//    }
//}
//
@Composable
fun HandleRegistrationResult(
    viewModel: AuthViewModel,
    context: Context,
    onRegister: Unit,
    onSuccess: () -> Unit
) {
    LaunchedEffect(viewModel.registrationResult) {
        viewModel.registrationResult.collectLatest { result ->
            onRegister.run { }
            result?.onSuccess { authResponse ->

                onSuccess()

                AuthUtils.saveUserDataAndToken(context, authResponse)

                Toast.makeText(context, "Inscription réussie", Toast.LENGTH_SHORT).show()

                viewModel.resetData()

            }?.onFailure {
                Toast.makeText(context, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
//
//@Composable
//fun HeaderSection() {
//    val localContext = LocalConfiguration.current
//
//    Column(
//        modifier = Modifier.padding(
//            top = localContext.screenHeightDp.dp / 8,
//            start = 16.dp,
//            end = 16.dp,
//            bottom = 20.dp
//        )
//    ) {
//        Text(
//            text = "Créer un mot de passe",
//            style = TextStyle(
//                fontSize = 40.sp,
//                color = MaterialTheme.colorScheme.primary,
//                fontWeight = FontWeight.Bold,
//            ),
//            textAlign = TextAlign.Start,
//        )
//        Spacer(modifier = Modifier.height(10.dp))
//        Text(
//            text = "Choisir un mot de passe fort et sécurisé.",
//            style = TextStyle(
//                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//            ),
//            textAlign = TextAlign.Start,
//        )
//    }
//}
//
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
                            MaterialTheme.colorScheme.surfaceVariant,
                            Color.Transparent
                        )
                    )
                )
                .fillMaxWidth()
        )
    }
}
//
//@Composable
//fun PasswordFieldsSection(
//    password: String,
//    confirmPassword: String,
//    hidePassword: Boolean,
//    hideConfirmPassword: Boolean,
//    onPasswordChange: (String) -> Unit,
//    onConfirmPasswordChange: (String) -> Unit,
//    onPasswordVisibilityChange: () -> Unit,
//    onConfirmPasswordVisibilityChange: () -> Unit
//) {
//    Column(modifier = Modifier.padding(20.dp)) {
//        PasswordTextField(
//            password = password,
//            onPasswordChange = onPasswordChange,
//            onTrailingIconClick = onPasswordVisibilityChange,
//            hidePassword = hidePassword,
//            label = "Mot de passe"
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        PasswordTextField(
//            password = confirmPassword,
//            onPasswordChange = onConfirmPasswordChange,
//            onTrailingIconClick = onConfirmPasswordVisibilityChange,
//            hidePassword = hideConfirmPassword,
//            label = "Confirmer le mot de passe"
//        )
//    }
//}
//
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
                    color = MaterialTheme.colorScheme.primary,
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight",
    showBackground = true
)
@Composable
fun RegisterStep3Preview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = AuthViewModel(
        context = context,
        authRepository = AuthRepository(
            sharedPreferences = context.getSharedPreferences(
                SharedPreferencesConstants.PREF_NAME,
                Context.MODE_PRIVATE
            )
        )
    )
    SportTrackTheme {
        RegisterStep3(
            context = context,
            navController = navController,
            viewModel = viewModel
        )
    }
}