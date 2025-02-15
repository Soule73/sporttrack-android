package com.stapp.sporttrack.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray

val AppTextInputColors: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = LightGray,
        unfocusedContainerColor = LightGray,
        cursorColor = BlueBlack,
        focusedLabelColor = BlueBlack,
        unfocusedLabelColor = BlueBlack,
        focusedBorderColor = BlueBlack,
        unfocusedBorderColor = LightGray,
        focusedLeadingIconColor = BlueBlack,
        unfocusedLeadingIconColor = BlueBlack,
        focusedTrailingIconColor = BlueBlack,
        unfocusedTrailingIconColor = BlueBlack,
        errorBorderColor = MaterialTheme.colorScheme.onBackground,
        errorTextColor = MaterialTheme.colorScheme.onBackground,
        errorLeadingIconColor = MaterialTheme.colorScheme.onBackground,
        errorTrailingIconColor = MaterialTheme.colorScheme.onBackground,
        errorLabelColor = MaterialTheme.colorScheme.onBackground,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
        focusedSupportingTextColor = BlueBlack,
        unfocusedSupportingTextColor = LightGray,
        focusedTextColor = BlueBlack,
        unfocusedTextColor = BlueBlack,
        disabledTextColor = BlueBlack.copy(alpha = 0.5f)
    )