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

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.theme.SportTrackTheme

/**
 * Displays a title and content, for use in conveying session details.
 */
fun LazyListScope.sessionDetailsItem(
  @StringRes labelId: Int,
  content: @Composable () -> Unit,
) {
  item {
    Text(
      text = stringResource(id = labelId),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.primary
    )
    content()
  }
}

@Preview
@Composable
fun SessionDetailsItemPreview() {
  SportTrackTheme {
    LazyColumn {
      sessionDetailsItem(R.string.total_steps) {
        Text(text = "12345")
      }
    }
  }
}
