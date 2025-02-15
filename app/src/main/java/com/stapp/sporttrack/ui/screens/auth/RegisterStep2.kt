package com.stapp.sporttrack.ui.screens.auth

import android.annotation.SuppressLint
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.components.GenderOption
import com.stapp.sporttrack.ui.components.SliderComponent
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray
import com.stapp.sporttrack.viewmodel.RegistrationViewModel

@SuppressLint("UnrememberedMutableInteractionSource", "UseOfNonLambdaOffsetOverload")
@Composable
fun RegisterStep2(navController: NavController, viewModel: RegistrationViewModel) {
    var gender by remember { mutableStateOf(viewModel.gender) }
    var height by remember { mutableFloatStateOf(viewModel.height) }
    var weight by remember { mutableFloatStateOf(viewModel.weight) }

    val heightSuffix = "cm"
    val weightSuffix = "kg"
    val minHeight = 50f
    val maxHeight = 500f
    val minWeight = 20f
    val maxWeight = 200f

    val isNextEnabled = gender.isNotEmpty()

    val sliderWeightValue = if (weight > 0) weight else minWeight
    val sliderHeightValue = if (height > 0) height else minHeight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "Donnez-nous quelques informations de base",
            style = TextStyle(
                fontSize = 25.sp,
                color = BlueBlack,
                fontWeight = FontWeight.ExtraBold
            ),
            textAlign = TextAlign.Start,
        )
        Column {
            Text("Genre", style = MaterialTheme.typography.titleMedium)
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
                    gender = "M"
                }
                GenderOption(
                    gender, "F",
                    R.drawable.woman,
                    "Female",
                    "Femme",
                ) {
                    gender = "F"
                }
            }
        }
        Column {
            SliderComponent(
                label = "Taille",
                value = sliderHeightValue,
                valueRange = minHeight..maxHeight,
                suffixLabel = heightSuffix,
                onValueChange = { height = it },
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
                onValueChange = { weight = it },
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
            IconButton(
                onClick = {
                    viewModel.gender = gender
                    viewModel.height = height
                    viewModel.weight = weight
                    navController.navigate("step3")
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = BlueBlack,
                    contentColor = LightGray,
                    disabledContentColor = BlueBlack.copy(alpha = 0.5f),
                    disabledContainerColor = LightGray
                ),
                enabled = isNextEnabled,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .background(BlueBlack, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(10.dp),
                    tint = Color.White,

                    )
            }
        }
    }
}