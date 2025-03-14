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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.theme.SportTrackTheme

/**
 * Shows the statistical min, max and average values, as can be returned from Health Platform.
 */
@Composable
fun ExerciseSessionDetailsMinMaxAvg(
  minimum: String?,
  maximum: String?,
  average: String?,
) {
  Row(
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(
      modifier = Modifier
        .weight(1f),
      text = stringResource(
        R.string.label_and_value,
        stringResource(R.string.min_label),
        minimum ?: "N/A"
      ),
      textAlign = TextAlign.Center
    )
    Text(
      modifier = Modifier
        .weight(1f),
      text = stringResource(
        R.string.label_and_value,
        stringResource(R.string.max_label),
        maximum ?: "N/A"
      ),
      textAlign = TextAlign.Center
    )
    Text(
      modifier = Modifier
        .weight(1f),
      text = stringResource(
        R.string.label_and_value,
        stringResource(R.string.avg_label),
        average ?: "N/A"
      ),
      textAlign = TextAlign.Center
    )
  }
}

@Preview
@Composable
fun ExerciseSessionDetailsMinMaxAvgPreview() {
  SportTrackTheme {
    ExerciseSessionDetailsMinMaxAvg(minimum = "10", maximum = "100", average = "55")
  }
}
