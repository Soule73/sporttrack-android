package com.stapp.sporttrack.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.data.models.ExerciseStatsUnit
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import com.stapp.sporttrack.ui.components.SummaryCard
import com.stapp.sporttrack.ui.components.WeeklyTrainingCalendar
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.screens.exercise.CustomListItem
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import com.stapp.sporttrack.viewmodel.toFormattedTime
import java.time.LocalDate

/**
 * Welcome screen shown when the app is first launched.
 */
@Composable
fun WelcomeScreen(
    modifier: Modifier,
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel
) {

    val stepsColor = MaterialTheme.colorScheme.primary
    val caloriesColor = MaterialTheme.colorScheme.secondary
    val durationColor = MaterialTheme.colorScheme.tertiary
    val heartRateColor = MaterialTheme.colorScheme.error
    val isLoading by exerciseViewModel.isLoading.collectAsStateWithLifecycle()

    val weeklyExerciseStats by exerciseViewModel.weeklyExerciseStats.collectAsState()

    LaunchedEffect(Unit) {
        exerciseViewModel.loadDailyExerciseStats()
        exerciseViewModel.loadWeeklyExerciseStats()
    }

    val dailyStats by exerciseViewModel.dailyExerciseStats.collectAsState()

    val trainingDates = dailyStats.filter { it.hasExercise }.map { it.date }.toSet()

    val configuration = LocalConfiguration.current
    LazyColumn(modifier = modifier) {
        item {
            if (isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryCard(
                        title = "",
                        value = "",
                        modifier = Modifier
                            .padding(6.dp)
                            .width((configuration.screenWidthDp.dp / 9) * 4)
                    )
                    SummaryCard(
                        title = "",
                        value = "",
                        modifier = Modifier
                            .padding(6.dp)
                            .width((configuration.screenWidthDp.dp / 9) * 4)
                    )
                }

            } else {
                weeklyExerciseStats?.let { stats ->
                    val summaryCardsData = listOf(
                        ExerciseSummaryCardData(
                            title = "Total des pas",
                            value = stats.totalSteps.toString(),
                            icon = R.drawable.ic_directions_walk,
                            color = stepsColor,
                        ),
                        ExerciseSummaryCardData(
                            title = "Calories brûlées",
                            value = stats.totalCalories.toString(),
                            unit = ExerciseStatsUnit.CALORIES,
                            icon = R.drawable.ic_local_dining,
                            color = caloriesColor,
                        ),
                        ExerciseSummaryCardData(
                            title = "Durée totale",
                            value = toFormattedTime(stats.totalDuration),
                            icon = R.drawable.baseline_flash_on_24,
                            color = durationColor,
                        ),
                        ExerciseSummaryCardData(
                            title = "Rythme cardiaque",
                            value = stats.totalRhythm.toString(),
                            unit = ExerciseStatsUnit.CADENCE,
                            icon = R.drawable.ic_heart_rate,
                            color = heartRateColor,
                        )
                    )
                    summaryCardsData.let { cardDatas ->
                        for (i in cardDatas.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SummaryCard(
                                    title = cardDatas[i].title,
                                    value = cardDatas[i].value,
                                    unit = cardDatas[i].unit,
                                    icon = cardDatas[i].icon,
                                    containerColor = cardDatas[i].color,
                                    contentColor = cardDatas[i].color,
                                    verticalPadding = 30.dp,
                                    titleFontSize = 12.sp,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .width((configuration.screenWidthDp.dp / 9) * 4)
                                )
                                if (i + 1 < cardDatas.size) {
                                    SummaryCard(
                                        verticalPadding = 30.dp,
                                        title = cardDatas[i + 1].title,
                                        value = cardDatas[i + 1].value,
                                        unit = cardDatas[i + 1].unit,
                                        icon = cardDatas[i + 1].icon,
                                        containerColor = cardDatas[i + 1].color,
                                        contentColor = cardDatas[i + 1].color,
                                        titleFontSize = 12.sp,
                                        modifier = Modifier
                                            .padding(vertical = 6.dp)
                                            .width((configuration.screenWidthDp.dp / 9) * 4)
                                    )
                                }
                            }

                        }
                    }
                }
            }
            WeeklyTrainingCalendar(
                trainingDates = trainingDates,
                currentDate = LocalDate.now(),
                onDaySelected = { day ->
                    if (day.hasTraining) {
                        navController.navigate("${Screen.DailyDetailsScreen.route}/${day.date}")
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp)
            )
            CustomListItem(
                isFirst = true,
                isLast = true,
                prefixIcon = {
                    Icon(
                        painter = painterResource(R.drawable.alarm_clock),
                        contentDescription = "Historique des exercices",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                },
                title = "Historique des entrainements",
                onClick = {
                    navController.navigate(Screen.TrainingDetailsScreen.route)
                }
            )
        }
    }
}