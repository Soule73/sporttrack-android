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
package com.stapp.sporttrack.data

import android.annotation.SuppressLint
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Creates a [ZonedDateTime] either using the offset stored in Health Connect, or falling back on
 * the zone offset for the device, where Health Connect contains no zone offset data. This fallback
 * may be correct in a number of circumstances, but may also not apply in others, so is used here
 * just as an example.
 */
fun dateTimeWithOffsetOrDefault(time: Instant, offset: ZoneOffset?): ZonedDateTime =
  if (offset != null) {
    ZonedDateTime.ofInstant(time, offset)
  } else {
    ZonedDateTime.ofInstant(time, ZoneId.systemDefault())
  }

@SuppressLint("DefaultLocale")
fun Duration.formatTime() = String.format(
  "%02d:%02d:%02d",
  this.toHours() % 24,
  this.toMinutes() % 60,
  this.seconds % 60
)

@SuppressLint("DefaultLocale")
fun Duration.formatHoursMinutes() = String.format(
  "%01dh%02dm",
  this.toHours() % 24,
  this.toMinutes() % 60
)
