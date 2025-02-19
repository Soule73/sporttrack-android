/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stapp.sporttrack.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.MIN_SUPPORTED_SDK
import com.stapp.sporttrack.ui.theme.SportTrackTheme

/**
 * Welcome text shown when the app first starts, where the device is not running a sufficient
 * Android version for Health Connect to be used.
 */
@Composable
fun NotSupportedMessage() {
    val tag = stringResource(R.string.not_supported_tag)
    val url = stringResource(R.string.not_supported_url)
    val context = LocalContext.current

    val notSupportedText = stringResource(
        id = R.string.not_supported_description,
        MIN_SUPPORTED_SDK
    )
    val notSupportedLinkText = stringResource(R.string.not_supported_link_text)

    val unavailableText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append(notSupportedText)
            append("\n\n")
        }
        pushStringAnnotation(tag = tag, annotation = url)
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(notSupportedLinkText)
        }
    }

    BasicText(
        text = unavailableText,
        style = TextStyle(textAlign = TextAlign.Justify),
        modifier = Modifier.clickable {
            unavailableText.getStringAnnotations(
                tag = "URL",
                start = 0,
                end = unavailableText.length
            )
                .firstOrNull()?.let {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                    )
                }
        }
    )
}


@Preview
@Composable
fun NotSupportedMessagePreview() {
    SportTrackTheme {
        NotSupportedMessage()
    }
}
