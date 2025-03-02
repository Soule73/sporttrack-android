package com.stapp.sporttrack.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CountdownAnimation(onCountdownFinish: () -> Unit) {
    var countdownValue by remember { mutableIntStateOf(3) }
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (countdownValue > 0) {
            delay(1000)
            countdownValue--
        }
        isAnimating = false
        onCountdownFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isAnimating) {
            val scale by rememberInfiniteTransition().animateFloat(
                initialValue = 1f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Text(
                text = countdownValue.toString(),
                fontSize = 200.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale)
            )
        }
    }
}
