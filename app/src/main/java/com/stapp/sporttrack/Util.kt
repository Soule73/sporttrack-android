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
package com.stapp.sporttrack

//import androidx.compose.material.ScaffoldState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.stapp.sporttrack.data.dateTimeWithOffsetOrDefault
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Shows details of a given throwable in the snackbar
 */
fun showExceptionSnackbar(
  scaffoldState: SnackbarHostState,

  scope: CoroutineScope,
  throwable: Throwable?,
) {
  scope.launch {
    scaffoldState.showSnackbar(
      message = throwable?.localizedMessage ?: "Unknown exception",
      duration = SnackbarDuration.Short
    )
  }
}

fun formatDisplayTimeStartEnd(
  startTime: Instant,
  startZoneOffset: ZoneOffset?,
  endTime: Instant,
  endZoneOffset: ZoneOffset?,
): String {
  val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
  val start = timeFormatter.format(dateTimeWithOffsetOrDefault(startTime, startZoneOffset))
  val end = timeFormatter.format(dateTimeWithOffsetOrDefault(endTime, endZoneOffset))
  return "$start - $end"
}
