package com.stapp.sporttrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun GenderOption(
    gender: String,
    option: String,
    imageRes: Int,
    contentDescription: String,
    label: String,
    onSelect: () -> Unit
) {
    val selected = gender == option
    val configuration = LocalConfiguration.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconToggleButton(
            checked = selected,
            onCheckedChange = { onSelect() },
            modifier = Modifier
                .width((configuration.screenWidthDp.dp/12)*5)
                .height(150.dp)
                .border(
                    width = 2.dp,
                    color = if (selected) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = if (selected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                )
                if (selected) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .clip(CircleShape),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.primary,
                        )
                    ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(5.dp)
                            .size(10.dp)

                    )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = label, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}