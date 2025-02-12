package com.stapp.sporttrack.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.stapp.sporttrack.R

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    onTrailingIconClick: () -> Unit,
    hidePassword: Boolean,
    label: String
) {
    val trailingIcon =
        if (hidePassword) R.drawable.eye_crossed
        else R.drawable.baseline_remove_red_eye_24

    val visualTransformation =
        if (hidePassword) PasswordVisualTransformation()
        else VisualTransformation.None

    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        value = password,
        onValueChange = onPasswordChange,
        leadingIcon = painterResource(id = R.drawable.baseline_lock_24),
        trailingIcon = painterResource(id = trailingIcon),
        onTrailingIconClick = onTrailingIconClick,
        label = { Text(label) },
        visualTransformation = visualTransformation,
    )
}