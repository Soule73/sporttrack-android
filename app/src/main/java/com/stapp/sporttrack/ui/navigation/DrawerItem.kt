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
package com.stapp.sporttrack.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.ui.theme.SportTrackTheme

/**
 * An item in the side navigation drawer.
 */
@Composable
fun DrawerItem(
    item: Screen,
    selected: Boolean,
    onItemClick: (Screen) -> Unit,
) {
  Row(
    modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { onItemClick(item) })
        .height(48.dp)
        .padding(start = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = stringResource(item.titleId),
      style = MaterialTheme.typography.titleSmall,
      color = if (selected) {
        MaterialTheme.colorScheme.primary
      } else {
        MaterialTheme.colorScheme.onBackground
      }
    )
  }
}

@Preview
@Composable
fun DrawerItemPreview() {
  SportTrackTheme {
    DrawerItem(
      item = Screen.ExerciseSessions,
      selected = true,
      onItemClick = {}
    )
  }
}
