package com.stapp.sporttrack.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.Day
import com.stapp.sporttrack.utils.formatWeeklyStartEndDate
import com.stapp.sporttrack.utils.getDaysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyTrainingCalendar(
    trainingDates: Set<LocalDate>,
    currentDate: LocalDate = LocalDate.now(),
    onDaySelected: (Day) -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(currentDate) }
    var currentWeekStart by remember { mutableStateOf(currentDate.with(DayOfWeek.MONDAY)) }

    val daysOfWeek = getDaysOfWeek(currentWeekStart, trainingDates)

    Column(modifier = modifier) {
        WeekNavigation(currentWeekStart, onPreviousWeek = {
            currentWeekStart = currentWeekStart.minusWeeks(1)
        }, onNextWeek = {
            currentWeekStart = currentWeekStart.plusWeeks(1)
        })
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(daysOfWeek) { day ->
                DayItem(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onClick = {
                        selectedDate = day.date
                        onDaySelected(day)
                    }
                )
            }
        }
    }
}

@Composable
fun DayItem(day: Day, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor =
        if (isSelected) CardDefaults.cardColors().containerColor.copy(0.5f) else Color.Transparent

    val dayOfWeek = day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1)
    val dayOfMonth = day.date.dayOfMonth.toString()
    val configuration = LocalConfiguration.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .width(configuration.screenWidthDp.dp / 8)
            .clickable(onClick = onClick)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 5.dp).height(80.dp)
    ) {
        Text(
            text = dayOfWeek.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = MaterialTheme.typography.bodySmall
        )
        if (day.hasTraining) {
            Badge(Modifier.size(10.dp), containerColor = Color.Green)
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }
        Text(
            text = dayOfMonth,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun WeekNavigation(
    currentWeekStart: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousWeek) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Semaine précédente")
        }
        Text(formatWeeklyStartEndDate(currentWeekStart), style = MaterialTheme.typography.titleSmall)
        IconButton(onClick = onNextWeek) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Semaine suivante")
        }
    }
}


@Composable
fun PreviewWeekCalendar() {
    // Dates de test pour les entraînements
    val trainingDates = setOf(
        LocalDate.now().minusDays(1),
        LocalDate.now().plusDays(2),
        LocalDate.now().plusDays(4)
    )

    WeeklyTrainingCalendar(
        trainingDates = trainingDates,
        currentDate = LocalDate.now(),
        onDaySelected = { day ->
            // Vous pouvez ajouter des actions ici si nécessaire
        },
        modifier = Modifier.padding(16.dp)
    )
}

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
fun PreviewWeekCalendarDisplay() {
    SportTrackTheme {
        PreviewWeekCalendar()
    }
}
