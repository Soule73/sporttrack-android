package com.stapp.sporttrack.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseSessionSectionButtons(
    isSessionActive: Boolean,
    isSessionPaused: Boolean,
    onClickPaused: () -> Unit,
    onClickResume: () -> Unit,
    onClickStart: () -> Unit,
    onClickFinished: () -> Unit,
    onClickSettings: () -> Unit,
    configuration: Configuration
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        if (!isSessionActive) {

            Button(
                onClick = onClickStart, modifier = Modifier
                    .padding(vertical = 6.dp)
                    .width((configuration.screenWidthDp.dp / 9) * 7)
            ) {
                Text(
                    text = "Démarrer",
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                )
            }

        } else {
            if (!isSessionPaused) {
                Button(
                    onClick = onClickPaused, modifier = Modifier
                        .padding(vertical = 6.dp)
                        .width((configuration.screenWidthDp.dp / 9) * 7)
                ) {
                    Text(
                        text = "Pause", modifier = Modifier
                            .padding(vertical = 5.dp)
                    )
                }

            } else {
                OutlinedButton(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width((configuration.screenWidthDp.dp / 5) * 2),
                    onClick = onClickResume,

                    ) {
                    Text(
                        text = "Reprendre", modifier = Modifier
                            .padding(vertical = 5.dp)
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width((configuration.screenWidthDp.dp / 5) * 2),

                    onClick = onClickFinished
                ) {
                    Text(
                        text = "Terminer", modifier = Modifier
                            .padding(vertical = 5.dp)
                    )
                }
            }
        }
        IconButton(
            onClick = onClickSettings,
            modifier = Modifier
                .size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .size(50.dp)
                    .padding(10.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}