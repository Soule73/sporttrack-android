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
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray

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
    maxIcon: Int
) {
    Column {
        Text(label, style = MaterialTheme.typography.headlineSmall)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = minIcon),
                contentDescription = "Min",
                modifier = Modifier.width(40.dp)
            )
            Image(
                painter = painterResource(id = maxIcon),
                contentDescription = "Max",
                modifier = Modifier.width(70.dp)
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
                    size = 35.dp,
                    modifier = Modifier
                        .background(
                            LightGray,
                            shape = CircleShape
                        )
                        .border(
                            width = 4.dp,
                            color = BlueBlack,
                            shape = CircleShape
                        )
                )
            },
            track = { sliderState ->
                Box(
                    modifier = Modifier
                        .track()
                        .background(BlueBlack.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .progress(sliderState = sliderState)
                            .background(
                                BlueBlack.copy(alpha = 0.2f)
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
                color = BlueBlack,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${maxLabel.toInt()} $suffixLabel",
                color = BlueBlack,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}