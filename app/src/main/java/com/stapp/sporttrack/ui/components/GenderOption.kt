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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.ui.theme.BlueBlack
import com.stapp.sporttrack.ui.theme.LightGray

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
                .size((configuration.screenWidthDp.dp / 3))
                .border(
                    width = 2.dp,
                    color = if (selected) Color.Transparent else LightGray,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = if (selected) LightGray else Color.Transparent,
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
                    modifier = Modifier.fillMaxSize()
                )
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .background(BlueBlack, CircleShape)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = label, style = MaterialTheme.typography.titleLarge)
    }
}