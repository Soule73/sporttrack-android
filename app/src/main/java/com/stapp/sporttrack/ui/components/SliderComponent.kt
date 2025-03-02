package com.stapp.sporttrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderComponent(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    suffixLabel: String,
    onValueChange: (Float) -> Unit,
    minLabel: Float,
    maxLabel: Float,
    minIcon: Int,
    maxIcon: Int,
    maxImageWidth: Dp = 40.dp,
    minImageWidth: Dp = 40.dp
) {
    Column {
        Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = minIcon),
                contentDescription = "Min",
                modifier = Modifier.width(minImageWidth),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Image(
                painter = painterResource(id = maxIcon),
                contentDescription = "Max",
                modifier = Modifier.width(maxImageWidth),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        CustomSlider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            valueRange = valueRange,
            showLabel = true,
            suffixLabel = suffixLabel,
            thumb = {
                CustomSliderDefaults.Thumb(
                    thumbValue = "",
                    color = Color.Transparent,
                    size = 30.dp,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                        .border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            },
            track = { sliderState ->
                Box(
                    modifier = Modifier
                        .track()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .progress(sliderState = sliderState)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                    )
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${minLabel.toInt()} $suffixLabel",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${maxLabel.toInt()} $suffixLabel",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}