package com.stapp.sporttrack.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable

val AppTextInputColors: TextFieldColors
    @Composable
    get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.3f),
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f),
        errorBorderColor = MaterialTheme.colorScheme.error.copy(0.3f))