package com.stapp.sporttrack.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.theme.SportTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight",
    showBackground = true
)
@Composable
fun MissingPermissionPreview(
) {
    SportTrackTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },

                )
            }
        ) {padding->
        MissingPermission(
            title = "Les permissions sont nécessaires pour cette fonctionnalité. Veuillez les activer dans les paramètres.",
            modifier = Modifier.padding(padding)
            .padding(horizontal = 10.dp),onClick = {},
            btnTitle = "Ouvrir les paramètres")
        }
    }
}

@Composable
fun MissingPermission(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    btnTitle: String,
    icon: Int? = null,
    title: String
){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography
                .titleMedium
                .copy(
                    fontWeight = FontWeight.Bold
                )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Icon(
            painter = painterResource(id =icon ?: R.drawable.user_gear),
            contentDescription = "User login image",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
        ) {
            Text(
                btnTitle,
                modifier = Modifier.padding(vertical = 10.dp),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }

    }
}