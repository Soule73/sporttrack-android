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
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
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
import com.stapp.sporttrack.ui.theme.SportTrackTheme

/**
 * Welcome text shown when the Health Connect APK is not yet installed on the device, prompting the user
 * to install it.
 */
@Composable
fun NotInstalledMessage() {
  val tag = stringResource(R.string.not_installed_tag)
  // Build the URL to allow the user to install the Health Connect package
  val url = Uri.parse(stringResource(id = R.string.market_url))
    .buildUpon()
    .appendQueryParameter("id", stringResource(id = R.string.health_connect_package))
    // Additional parameter to execute the onboarding flow.
    .appendQueryParameter("url", stringResource(id = R.string.onboarding_url))
    .build()
  val context = LocalContext.current

  val notInstalledText = stringResource(id = R.string.not_installed_description)
  val notInstalledLinkText = stringResource(R.string.not_installed_link_text)

  val unavailableText = buildAnnotatedString {
    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
      append(notInstalledText)
      append("\n\n")
    }
    pushStringAnnotation(tag = tag, annotation = url.toString())
    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
      append(notInstalledLinkText)
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
fun NotInstalledMessagePreview() {
  SportTrackTheme {
    NotInstalledMessage()
  }
}
