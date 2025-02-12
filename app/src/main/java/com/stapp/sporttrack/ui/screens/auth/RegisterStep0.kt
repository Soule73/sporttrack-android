package com.stapp.sporttrack.ui.screens.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray

@SuppressLint("UnrememberedMutableInteractionSource", "UseOfNonLambdaOffsetOverload")
@Composable
fun RegisterStep0(navController: NavController) {
    Column {
        val configuration = LocalConfiguration.current

        Column(
            modifier = Modifier
                .height(configuration.screenHeightDp.dp / 2)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome_image),
                contentDescription = "Welcome Image",
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                LightGray,
                                LightGray,
                                Color.Transparent
                            )
                        )
                    )
                    .fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, bottom = 40.dp)
                .height(configuration.screenHeightDp.dp / 2)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "Bienvenue sur votre outil de suivi de données sportives.",
                style = TextStyle(
                    fontSize = 40.sp,
                    color = BlueBlack,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Start,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = { navController.navigate("step1") },
                    modifier = Modifier
                        .width(70.dp)
                        .height(70.dp)
                        .background(BlueBlack, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
