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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Displays summary information about the [ExerciseSessionRecord]
 */

@Composable
fun ExerciseSessionInfoColumn(
    start: ZonedDateTime,
    end: ZonedDateTime,
    uid: String,
    sourcePackageName: String,
    name: String,
    onClick: (String) -> Unit = {},
) {
  Column(
    modifier = Modifier.clickable {
      onClick(uid)
    }
  ) {
    Text(
      color = MaterialTheme.colorScheme.primary,
      text = "${start.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS)} - ${end.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS)}",
      style = MaterialTheme.typography.bodySmall
    )
    Text(name)
    Text(sourcePackageName)
    Text(uid)
  }
}

@Preview
@Composable
fun ExerciseSessionInfoColumnPreview() {
    SportTrackTheme {
    ExerciseSessionInfoColumn(
      ZonedDateTime.now().minusMinutes(30),
      ZonedDateTime.now(),
      UUID.randomUUID().toString(),
      "com.stapp.sporttrack",
      "Running"
    )
  }
}
