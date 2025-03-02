package com.stapp.sporttrack.ui.screens.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.stapp.sporttrack.ui.components.GenderOption
import com.stapp.sporttrack.ui.components.SliderComponent
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.AuthUtils
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedMutableInteractionSource", "UseOfNonLambdaOffsetOverload")
@Composable
fun RegisterStep2(
    context: Context,
    navController: NavController,
    viewModel: AuthViewModel,
    isAuthenticated: Boolean = false
) {
    val gender by viewModel.gender.collectAsState()
    val height by viewModel.height.collectAsState()
    val weight by viewModel.weight.collectAsState()

    val heightSuffix = "cm"
    val weightSuffix = "kg"
    val minHeight = 50f
    val maxHeight = 500f
    val minWeight = 20f
    val maxWeight = 200f

    val isNextEnabled = gender.isNotEmpty()

    val sliderWeightValue = if (weight > 0) weight else minWeight
    val sliderHeightValue = if (height > 0) height else minHeight

    var isLoading by remember { mutableStateOf(false) }

    fun setIsLoadingFalse() {
        isLoading = false
    }
    if (isAuthenticated) {

        HandleUpdateResult(viewModel, context, { setIsLoadingFalse() }, onSuccess = {
            navController.navigate(
                Screen.ProfileScreen.route
            )
        })
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = if (isAuthenticated) "Modifier vos informations" else "Donnez-nous quelques informations de base",
            style = TextStyle(
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            ),
            textAlign = TextAlign.Start,
        )
        Column {
            Text(
                "Genre",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                GenderOption(
                    gender, "M",
                    R.drawable.man,
                    "Male",
                    "Homme"
                ) {
                    viewModel.setGender("M")
                }
                Spacer(modifier = Modifier.width(10.dp))
                GenderOption(
                    gender, "F",
                    R.drawable.woman,
                    "Female",
                    "Femme",
                ) {
                    viewModel.setGender("F")
                }
            }
        }
        Column {
            SliderComponent(
                label = "Taille",
                value = sliderHeightValue,
                valueRange = minHeight..maxHeight,
                suffixLabel = heightSuffix,
                onValueChange = { viewModel.setHeight(it) },
                minLabel = minHeight,
                maxLabel = maxHeight,
                minIcon = R.drawable.man,
                maxIcon = R.drawable.man,
                maxImageWidth = 50.dp,
                minImageWidth = 30.dp
            )
        }
        Column {
            SliderComponent(
                label = "Poids",
                value = sliderWeightValue,
                valueRange = minWeight..maxWeight,
                suffixLabel = weightSuffix,
                onValueChange = { viewModel.setWeight(it) },
                minLabel = minWeight,
                maxLabel = maxWeight,
                minIcon = R.drawable.man,
                maxIcon = R.drawable.big_man,
                maxImageWidth = 30.dp,
                minImageWidth = 30.dp
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            if (isAuthenticated) {
                Button(
                    onClick = {
                        isLoading = true

                        viewModel.update()

                    },
                    enabled = !isLoading,
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
                                "En cours...",
                                modifier = Modifier
                                    .padding(start = 10.dp),
                            )
                        }
                    } else {
                        Text(
                            "Sauvegarder",
                            modifier = Modifier
                                .padding(vertical = 10.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                }
            } else {
                IconButton(
                    onClick = {
                        navController.navigate(Screen.RegisterScreenStep3.route)
                    },
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun HandleUpdateResult(
    viewModel: AuthViewModel,
    context: Context,
    onUpdate: () -> Unit,
    onSuccess: () -> Unit
) {
    LaunchedEffect(viewModel.updateResult) {
        viewModel.updateResult.collectLatest { result ->
            onUpdate()
            result?.onSuccess { userResponse ->
                AuthUtils.saveUserData(context, userResponse)
                viewModel.resetData()
                Toast.makeText(context, "Mise à jour réussie", Toast.LENGTH_SHORT).show()
                onSuccess()
            }?.onFailure { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
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
fun RegisterStep2Preview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = AuthViewModel(
        context = context,
        authRepository = AuthRepository(
            sharedPreferences = context.getSharedPreferences(
                SharedPreferencesConstants.PREF_NAME,
                Context.MODE_PRIVATE
            ),
        )
    )
    SportTrackTheme {

        RegisterStep2(context, navController, viewModel)
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
fun UpdateInfoPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel = AuthViewModel(
        context = LocalContext.current,
        authRepository = AuthRepository(
            sharedPreferences = context.getSharedPreferences(
                SharedPreferencesConstants.PREF_NAME,
                Context.MODE_PRIVATE
            ),
        )
    )
    SportTrackTheme {

        RegisterStep2(context, navController, viewModel, true)
    }
}