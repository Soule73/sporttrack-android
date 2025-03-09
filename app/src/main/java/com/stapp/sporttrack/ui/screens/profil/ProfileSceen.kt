package com.stapp.sporttrack.ui.screens.profil

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DevicesFold
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.UserResponse
import com.stapp.sporttrack.ui.components.PasswordTextField
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.screens.CustomBottomNavigationBar
import com.stapp.sporttrack.ui.screens.exercise.CustomListItem
import com.stapp.sporttrack.ui.theme.SettingsViewModel
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.utils.capitalizeFirstChar
import com.stapp.sporttrack.utils.hideKeyboard
import com.stapp.sporttrack.viewmodel.AuthViewModel
import com.stapp.sporttrack.viewmodel.AuthViewModelFactory

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    context: Context
) {
    val configuration = LocalConfiguration.current
    val scrollState = rememberScrollState()
    val userData by authViewModel.userData.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(top = 50.dp)
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(configuration, userData)
        Spacer(modifier = Modifier.height(30.dp))
        ProfileOptions(navController, authViewModel)
        Spacer(modifier = Modifier.height(15.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = CardDefaults.cardColors().containerColor.copy(
                    0.3f
                )
            )
        ) {
            ThemeSettingsSection(settingsViewModel)
        }
        Spacer(modifier = Modifier.height(15.dp))
        LogoutOption(navController, authViewModel)
        Spacer(modifier = Modifier.height(15.dp))
        DeleteAccount(
            viewModel = authViewModel,
            navController = navController,
            context = context
        )
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun ProfileHeader(configuration: Configuration, userData: UserResponse?) {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "User profile",
        tint = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .size(configuration.screenWidthDp.dp / 3)
            .padding(10.dp)
            .background(MaterialTheme.colorScheme.onSurface, CircleShape)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "${
            userData?.firstName?.capitalizeFirstChar().orEmpty()
        } ${userData?.lastName?.capitalizeFirstChar().orEmpty()}",
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = userData?.email.orEmpty(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun ProfileOptions(navController: NavController, authViewModel: AuthViewModel) {
    CustomListItem(
        title = stringResource(R.string.update_track_info),
        isFirst = true,
        isLast = false,
        onClick = {
            authViewModel.beginUpdate()
            navController.navigate(Screen.RegisterScreenStep1.route)
        },
        prefixIcon = { ListIcon(Icons.Default.Edit, MaterialTheme.colorScheme.tertiary) },
        endIcon = { ListIcon(Icons.Default.ChevronRight, Color.Gray) }
    )
    CustomListItem(
        title = stringResource(R.string.update_password),
        isFirst = false,
        isLast = false,
        onClick = { navController.navigate(Screen.RegisterScreenStep3.route) },
        prefixIcon = { ListIcon(Icons.Default.Security, MaterialTheme.colorScheme.tertiary) },
        endIcon = { ListIcon(Icons.Default.ChevronRight, Color.Gray) }
    )
    CustomListItem(
        title = stringResource(R.string.privacy_policy),
        isFirst = false,
        isLast = true,
        onClick = { navController.navigate(Screen.PrivacyPolicy.route) },
        prefixIcon = { ListIcon(Icons.Default.Security, MaterialTheme.colorScheme.tertiary) },
        endIcon = { ListIcon(Icons.Default.ChevronRight, Color.Gray) }
    )
}

@Composable
fun ListIcon(imageVector: ImageVector, tint: Color) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = tint,
        modifier = Modifier
            .size(40.dp)
            .padding(5.dp)
    )
}

@Composable
fun LogoutOption(navController: NavController, authViewModel: AuthViewModel) {
    CustomListItem(
        title = stringResource(R.string.logout),
        isFirst = true,
        isLast = true,
        color = MaterialTheme.colorScheme.error,
        onClick = {
            authViewModel.logout()
            navController.navigate(Screen.LoginScreen.route)
        },
        prefixIcon = { ListIcon(Icons.Default.DevicesFold, MaterialTheme.colorScheme.error) },
        endIcon = { ListIcon(Icons.Default.ChevronRight, MaterialTheme.colorScheme.error) }
    )
}

@Composable
fun ThemeSettingsSection(viewModel: SettingsViewModel) {
    val themePreference by viewModel.themePreference.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Choisissez le thème", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        RadioButtonGroup(
            options = listOf(
                SharedPreferencesConstants.PREF_SYSTEM,
                SharedPreferencesConstants.PREF_LIGHT_MODE,
                SharedPreferencesConstants.PREF_DARK_MODE
            ),
            selectedOption = themePreference,
            onOptionSelected = { option -> viewModel.setThemePreference(option) }
        )
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    options.forEach { option ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOptionSelected(option) }
        ) {
            val optionLabel = when (option) {
                SharedPreferencesConstants.PREF_SYSTEM -> "Suivre le système"
                SharedPreferencesConstants.PREF_LIGHT_MODE -> "Mode Clair"
                SharedPreferencesConstants.PREF_DARK_MODE -> "Mode Sombre"
                else -> option
            }
            RadioButton(
                selected = selectedOption == option,
                onClick = { onOptionSelected(option) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    unselectedColor = RadioButtonDefaults.colors().unselectedColor,
                    disabledSelectedColor = RadioButtonDefaults.colors().disabledSelectedColor,
                    disabledUnselectedColor = RadioButtonDefaults.colors().disabledUnselectedColor
                )
            )
            Text(text = optionLabel, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccount(navController: NavController, viewModel: AuthViewModel, context: Context) {

    val password by viewModel.password.collectAsState()
    var hidePassword by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }

    val deleteAccountResult by viewModel.deleteAccountResult.collectAsStateWithLifecycle()

    var showDeleteAccount by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(deleteAccountResult) {
        deleteAccountResult?.let { result ->
            result.onSuccess {
                Toast.makeText(context, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                isLoading = false
                sheetState.hide()
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(0)
                }
            }.onFailure {e->
                isLoading = false
                println("erreur lors de la suppression du compte : ${e.cause} ${e.message}")
                Toast.makeText(
                    context,
                    "Erreur lors de la suppression du compte",
                    Toast.LENGTH_SHORT
                ).show()
            }
            viewModel.resetDeleteAccountResult()
        }
    }

    if (showDeleteAccount) {
        ModalBottomSheet(
            sheetState = sheetState,
            content = {

                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Attention", style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start
                    )
                    Text(
                        "Cette action est irréversible. Toutes vos données seront définitivement supprimées et il sera impossible de les restaurer.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordTextField(
                        password = password,
                        onPasswordChange = { viewModel.setPassword(it) },
                        onTrailingIconClick = { hidePassword = !hidePassword },
                        hidePassword = hidePassword,
                        label = "Votre Mot de passe"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        onClick = {
                            isLoading = true
                            hideKeyboard(context)
                            viewModel.deleteAccount(password)
                        },
                        enabled = password.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
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
                                    modifier = Modifier.padding(start = 10.dp),
                                )
                            }
                        } else {
                            Text(
                                "Supprimer le compte",
                                modifier = Modifier.padding(vertical = 10.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        }
                    }
                }
            },
            onDismissRequest = { showDeleteAccount = false },
        )
    }

    CustomListItem(
        title = stringResource(R.string.delete_account),
        isFirst = true,
        isLast = true,
        color = MaterialTheme.colorScheme.error,
        onClick = {
            showDeleteAccount = true
        },
        prefixIcon = { ListIcon(Icons.Default.PersonOff, MaterialTheme.colorScheme.error) },
        endIcon = { ListIcon(Icons.Default.ChevronRight, MaterialTheme.colorScheme.error) }
    )
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
fun ProfileScreenPreview() {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    viewModelStoreOwner?.let {
        val context = LocalContext.current
        val sharedPreferences =
            context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, MODE_PRIVATE)
        val authViewModel: AuthViewModel = viewModel(
            it, factory = AuthViewModelFactory(
                context, sharedPreferences
            )
        )
        val settingsViewModel = SettingsViewModel(context)
        SportTrackTheme {
            Scaffold(
                content = { paddingValues ->
                    ProfileScreen(
                        navController = rememberNavController(),
                        authViewModel = authViewModel,
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(top = 10.dp)
                            .padding(horizontal = 10.dp),
                        settingsViewModel = settingsViewModel,
                        context = LocalContext.current
                    )
                },
                bottomBar = { CustomBottomNavigationBar(navController = rememberNavController()) }
            )
        }
    }
}