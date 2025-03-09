package com.stapp.sporttrack.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: Painter? = null,
    onTrailingIconClick: () -> Unit = {},
    trailingIcon: Painter? = null,
    label: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(

        visualTransformation = visualTransformation,
        modifier = modifier,
        value = value,
        label = label,
        onValueChange = onValueChange,
        colors = AppTextInputColors,
        maxLines = 1,
        singleLine = true,
        leadingIcon = {
            if (leadingIcon != null)
                Icon(
                    painter = leadingIcon,
                    contentDescription = null
                )
        },
        trailingIcon = {
            if (trailingIcon != null)
                IconButton(onClick = onTrailingIconClick) {
                    Icon(
                        painter = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
        }
    )
}