package com.stapp.sporttrack.ui.screens.welcome

import android.app.Activity.MODE_PRIVATE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stapp.sporttrack.data.models.DailyExerciseDetailsResponse
import com.stapp.sporttrack.data.models.ExerciseStatsUnit
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import com.stapp.sporttrack.data.repository.ExerciseRepository
import com.stapp.sporttrack.ui.components.ErrorFullScreen
import com.stapp.sporttrack.ui.components.LoadingFullScreen
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.screens.exercise.ExerciseSummaryGrid
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import com.stapp.sporttrack.viewmodel.toFormattedTime
import java.time.LocalDate
import java.util.Locale

@Composable
fun DailyDetailsScreen(
    date: LocalDate,
    exerciseViewModel: ExerciseViewModel,
    modifier: Modifier,
    navController: NavHostController
) {
    val dailyExerciseDetails by exerciseViewModel.dailyExerciseDetails.collectAsState()
    val isLoading by exerciseViewModel.isLoading.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current

    LaunchedEffect(date) {
        exerciseViewModel.loadDailyExerciseDetails(date)
    }

    if (isLoading && dailyExerciseDetails != null) {
        LoadingFullScreen(modifier = modifier)
    } else {
        dailyExerciseDetails?.let { details ->
            LazyColumn(modifier = modifier.fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(
                    MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )) {
                val summaryCardsData = generateSummaryCardsData(stats = details)

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardDefaults.cardColors().containerColor.copy(0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(
                                text = "Détails des entraînements",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 15.dp)
                            )
                            ExerciseSummaryGrid(
                                configuration = configuration,
                                summaryCardsData = summaryCardsData
                            )

                        }
                    }
                }

                if (details.sessions.isEmpty()) {
                    item {
                        Text("Aucune session trouvée pour cette date.")
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = CardDefaults.cardColors().containerColor.copy(0.3f)
                            )
                        ) {
                            Text(
                                "Liste des sessions", modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                                    .padding(8.dp),
                                style = MaterialTheme.typography.titleSmall,
                                )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                details.sessions.forEach { session ->
                                    TrainingItem(session) {
                                        exerciseViewModel.loadExerciseSessionById(session.sessionId)
                                        navController.navigate(Screen.ExerciseSessionDetail.route)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    } ?: run {
        ErrorFullScreen(modifier = modifier, title = "Aucun détail disponible pour cette date.")
    }
}


fun generateSummaryCardsData(stats: DailyExerciseDetailsResponse): List<ExerciseSummaryCardData> {
    val summaryCardsData = mutableListOf<ExerciseSummaryCardData>()

    if (stats.totalDuration != 0) {
        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Durée",
                value = toFormattedTime(stats.totalDuration),
            )
        )
    }
    if (stats.totalDistance != 0.0) {
        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Distance",
                value = String.format(Locale.getDefault(), "%.2f", stats.totalDistance),
                unit = ExerciseStatsUnit.DISTANCE
            )
        )
    }

    if (stats.totalSteps != 0) {
        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Pas",
                value = stats.totalSteps.toString()
            )
        )
    }

    if (stats.totalCadence != 0.0) {
        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Cadence",
                value = String.format(Locale.getDefault(), "%.2f", stats.totalCadence),
                unit = ExerciseStatsUnit.CADENCE
            )
        )
    }

    if (stats.totalRhythm != 0.0) {
        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Rythme",
                value = String.format(Locale.getDefault(), "%.2f", stats.totalRhythm),
                unit = ExerciseStatsUnit.RHYTHM
            )
        )
    }

    if (stats.totalSlope != 0.0) {
        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Pente",
                value = String.format(Locale.getDefault(), "%.2f", stats.totalSlope),
                unit = ExerciseStatsUnit.SLOPE
            )
        )
    }

    summaryCardsData.add(
        ExerciseSummaryCardData(
            title = "Calories brûlées",
            value = String.format(Locale.getDefault(), "%.2f", stats.totalCalories),
            unit = ExerciseStatsUnit.CALORIES
        )
    )


    return summaryCardsData
}

@Preview(showBackground = true)
@Composable
fun DailyDetailsScreenPreview() {
    SportTrackTheme {
        DailyDetailsScreen(
            date = LocalDate.now(),
            exerciseViewModel = ExerciseViewModel(
                exerciseRepository = ExerciseRepository(
                    LocalContext.current.getSharedPreferences(
                        SharedPreferencesConstants.PREF_NAME,
                        MODE_PRIVATE
                    )
                )

            ),
            modifier = Modifier,
            navController = rememberNavController()
        )
    }
}