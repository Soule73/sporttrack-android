package com.stapp.sporttrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


/**
 * A custom slider composable that allows selecting a value within a given range.
 * link article : (https://proandroiddev.com/building-custom-sliders-in-jetpack-compose-a-deep-dive-234ced73b54f)
 * @param value The current value of the slider.
 * @param onValueChange The callback invoked when the value of the slider changes.
 * @param modifier The modifier to be applied to the slider.
 * @param valueRange The range of values the slider can represent.
 * @param gap The spacing between indicators on the slider.
 * @param showIndicator Determines whether to show indicators on the slider.
 * @param showLabel Determines whether to show a label above the slider.
 * @param suffixLabel suffix label.
 * @param enabled Determines whether the slider is enabled for interaction.
 * @param thumb The composable used to display the thumb of the slider.
 * @param track The composable used to display the track of the slider.
 * @param indicator The composable used to display the indicators on the slider.
 *
 **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    gap: Int = 1,
    showIndicator: Boolean = false,
    showLabel: Boolean = false,
    suffixLabel: String = "",
    enabled: Boolean = true,
    thumb: @Composable (thumbValue: Int) -> Unit = {
        CustomSliderDefaults.Thumb(it.toString())
    },
    track: @Composable (sliderState: SliderState) -> Unit = { sliderState ->
        CustomSliderDefaults.Track(sliderState = sliderState)
    },
    indicator: @Composable (indicatorValue: Int) -> Unit = { indicatorValue ->
        CustomSliderDefaults.Indicator(indicatorValue = indicatorValue.toString())
    }
) {
    val itemCount = (valueRange.endInclusive - valueRange.start).roundToInt()
    val steps = if (gap == 1) 0 else (itemCount / gap - 1)

    Box(modifier = modifier) {
        Layout(
            measurePolicy = customSliderMeasurePolicy(
                itemCount = itemCount,
                gap = gap,
                value = value,
                startValue = valueRange.start
            ),
            content = {
                if (showLabel)
                    Label(
                        modifier = Modifier.layoutId(CustomSliderComponents.LABEL),
                        value = value,
                        suffixLabel = suffixLabel
                    )

                Box(modifier = Modifier.layoutId(CustomSliderComponents.THUMB)) {
                    thumb(value.roundToInt())
                }

                Slider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId(CustomSliderComponents.SLIDER),
                    value = value,
                    valueRange = valueRange,
                    steps = steps,
                    onValueChange = { onValueChange(it) },
                    thumb = {
                        thumb(value.roundToInt())
                    },
                    track = { track(it) },
                    enabled = enabled
                )

                if (showIndicator)
                    Indicator(
                        modifier = Modifier.layoutId(CustomSliderComponents.INDICATOR),
                        valueRange = valueRange,
                        gap = gap,
                        indicator = indicator
                    )
            })
    }
}

@Composable
private fun Label(
    modifier: Modifier = Modifier,
    value: Float,
    suffixLabel: String = ""
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${value.roundToInt()}$suffixLabel",
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Indicator(
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float>,
    gap: Int,
    indicator: @Composable (indicatorValue: Int) -> Unit
) {
    for (i in valueRange.start.roundToInt()..valueRange.endInclusive.roundToInt() step gap) {
        Box(
            modifier = modifier
        ) {
            indicator(i)
        }
    }
}

/**
 * Object to hold defaults used by [CustomSlider]
 */
object CustomSliderDefaults {
    @Composable
    fun Thumb(
        thumbValue: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.primary,
        size: Dp = 30.dp,
        shape: Shape = CircleShape,
        content: @Composable () -> Unit = {
            Text(
                text = thumbValue,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    ) {
        Box(
            modifier = modifier
                .thumb(size = size, shape = shape)
                .background(color)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Track(
        sliderState: SliderState,
        modifier: Modifier = Modifier,
        trackColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        progressColor: Color = MaterialTheme.colorScheme.primary,
        height: Dp = 8.dp,
        shape: Shape = CircleShape
    ) {
        Box(
            modifier = modifier
                .track(height = height, shape = shape)
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .progress(
                        sliderState = sliderState,
                        height = height,
                        shape = shape
                    )
                    .background(progressColor)
            )
        }
    }

    @Composable
    fun Indicator(
        indicatorValue: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodySmall
    ) {
        Box(modifier = modifier) {
            Text(
                text = indicatorValue,
                style = style,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    @Composable
    fun Label(
        labelValue: String,
        modifier: Modifier = Modifier,
        style: TextStyle = MaterialTheme.typography.bodySmall
    ) {
        Box(modifier = modifier) {
            Text(
                text = labelValue,
                style = style,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun Modifier.track(
    height: Dp = 8.dp,
    shape: Shape = CircleShape
) = this
    .fillMaxWidth()
    .heightIn(min = height)
    .clip(shape)

@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.progress(
    sliderState: SliderState,
    height: Dp = 8.dp,
    shape: Shape = CircleShape
) = this
    .fillMaxWidth(fraction = (sliderState.value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start))
    .heightIn(min = height)
    .clip(shape)

fun Modifier.thumb(
    size: Dp = 30.dp,
    shape: Shape = CircleShape
) = this
    .defaultMinSize(minWidth = size, minHeight = size)
    .clip(shape)

private fun customSliderMeasurePolicy(
    itemCount: Int,
    gap: Int,
    value: Float,
    startValue: Float
) = MeasurePolicy { measurables, constraints ->
    // Measure the thumb component and calculate its radius.
    val thumbPlaceable = measurables.first {
        it.layoutId == CustomSliderComponents.THUMB
    }.measure(constraints)
    val thumbRadius = (thumbPlaceable.width / 2).toFloat()

    val indicatorPlaceables = measurables.filter {
        it.layoutId == CustomSliderComponents.INDICATOR
    }.map { measurable ->
        measurable.measure(constraints)
    }
    val indicatorHeight = indicatorPlaceables.maxByOrNull { it.height }?.height ?: 0

    val sliderPlaceable = measurables.first {
        it.layoutId == CustomSliderComponents.SLIDER
    }.measure(constraints)
    val sliderHeight = sliderPlaceable.height

    val labelPlaceable = measurables.find {
        it.layoutId == CustomSliderComponents.LABEL
    }?.measure(constraints)
    val labelHeight = labelPlaceable?.height ?: 0

    // Calculate the total width and height of the custom slider layout
    val width = sliderPlaceable.width
    val height = labelHeight + sliderHeight + indicatorHeight

    // Calculate the available width for the track (excluding thumb radius on both sides).
    val trackWidth = width - (2 * thumbRadius)

    // Calculate the width of each section in the track.
    val sectionWidth = trackWidth / itemCount
    // Calculate the horizontal spacing between indicators.
    val indicatorSpacing = sectionWidth * gap

    // To calculate offset of the label, first we will calculate the progress of the slider
    // by subtracting startValue from the current value.
    // After that we will multiply this progress by the sectionWidth.
    // Add thumb radius to this resulting value.
    val labelOffset = (sectionWidth * (value - startValue)) + thumbRadius

    layout(width = width, height = height) {
        var indicatorOffsetX = thumbRadius
        // Place label at top.
        // We have to subtract the half width of the label from the labelOffset,
        // to place our label at the center.
        labelPlaceable?.placeRelative(
            x = (labelOffset - (labelPlaceable.width / 2)).roundToInt(),
            y = 0
        )

        // Place slider placeable below the label.
        sliderPlaceable.placeRelative(x = 0, y = labelHeight)

        // Place indicators below the slider.
        indicatorPlaceables.forEach { placeable ->
            // We have to subtract the half width of the each indicator from the indicatorOffset,
            // to place our indicators at the center.
            placeable.placeRelative(
                x = (indicatorOffsetX - (placeable.width / 2)).roundToInt(),
                y = labelHeight + sliderHeight
            )
            indicatorOffsetX += indicatorSpacing
        }
    }
}

private enum class CustomSliderComponents {
    SLIDER, LABEL, INDICATOR, THUMB
}