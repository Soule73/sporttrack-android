package com.stapp.sporttrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.ui.theme.SportTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoPauseSettingsBottomSheet(
    isAutoPauseEnabled: Boolean,
    onAutoPauseChanged: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    state: SheetState,
) {
    ModalBottomSheet(
        sheetState = state,
        content = {
            BottomSheetContent(
                checked = isAutoPauseEnabled,
                onAutoPauseChanged = onAutoPauseChanged,
                onDismissRequest = onDismissRequest
            )
        },
        onDismissRequest = onDismissRequest,
    )
}

@Composable
fun BottomSheetContent(
    checked: Boolean,
    onAutoPauseChanged: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Paramètres",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            IconButton(
                onClick = onDismissRequest,
            ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = checked,
                onCheckedChange = onAutoPauseChanged,
                thumbContent = if (checked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Activer la pause automatique",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

    }
}
