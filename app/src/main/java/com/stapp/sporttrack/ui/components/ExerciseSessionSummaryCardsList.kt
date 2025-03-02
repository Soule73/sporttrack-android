package com.stapp.sporttrack.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData

@Composable
fun ExerciseSessionSummaryCardsList(
    cardsData: List<ExerciseSummaryCardData>,
    modifier: Modifier = Modifier,
    configuration: Configuration
) {

    Column(modifier = modifier) {
        if (cardsData.size == 2) {
            for (i in cardsData.indices) {
                SummaryCard(
                    title = cardsData[i].title,
                    value = cardsData[i].value,
                    unit = cardsData[i].unit,
                    icon = cardsData[i].icon,
                    centerContent = true,
                    valueFontSize = 40.sp,
                    titleFontSize = 20.sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .height((configuration.screenHeightDp.dp / 13) * 5)
                )
            }
        } else {
            for (i in cardsData.indices step 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryCard(
                        title = cardsData[i].title,
                        value = cardsData[i].value,
                        unit = cardsData[i].unit,
                        icon = cardsData[i].icon,
                        verticalPadding = 15.dp,
                        titleFontSize = 15.sp,
                        modifier = Modifier
                            .padding(vertical =  6.dp)
                            .width((configuration.screenWidthDp.dp / 9) * 4)
                    )
                    if (i + 1 < cardsData.size) {
                        SummaryCard(
                            title = cardsData[i + 1].title,
                            value = cardsData[i + 1].value,
                            unit = cardsData[i + 1].unit,
                            icon = cardsData[i + 1].icon,
                            verticalPadding = 15.dp,
                            titleFontSize = 15.sp,
                            modifier = Modifier
                                .padding(vertical =  6.dp)
                                .width((configuration.screenWidthDp.dp / 9) * 4)
                        )
                    }
                }
            }
        }
    }
}