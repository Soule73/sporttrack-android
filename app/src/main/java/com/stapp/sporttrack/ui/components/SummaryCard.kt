package com.stapp.sporttrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard(
    title: String,
    value: String,
    unit: String? = "",
    icon: Int? = null,
    containerColor: Color?=null,
    contentColor: Color? =null,
    modifier: Modifier,
    verticalPadding: Dp = 10.dp,
    titleFontSize: TextUnit = 10.sp,
    valueFontSize: TextUnit = 24.sp,
    centerContent:Boolean=false,
) {
    Card(
        colors = if (containerColor != null) {
            CardDefaults.cardColors(
                containerColor = containerColor.copy(alpha = 0.2f),
                contentColor = contentColor ?: MaterialTheme.colorScheme.onSurface
            )
        } else {
            CardDefaults.cardColors(
                containerColor = CardDefaults.cardColors().containerColor.copy( 0.3f)
            )
        },
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier= if(centerContent){
                Modifier
                    .padding(vertical = verticalPadding)
                    .fillMaxHeight()
            }else{
                Modifier
                    .padding(vertical = verticalPadding)
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            ) {
                if (icon != null) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        modifier = Modifier.size(25.dp),
                        tint = contentColor ?: MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = titleFontSize,
                    color = contentColor ?: MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "$value ${unit ?: ""}",
                fontSize = valueFontSize,
                color = contentColor ?: MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}