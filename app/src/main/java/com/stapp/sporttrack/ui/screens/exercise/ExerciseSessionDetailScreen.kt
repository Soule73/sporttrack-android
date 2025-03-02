package com.stapp.sporttrack.ui.screens.exercise

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.stapp.sporttrack.data.models.ExerciseSessionResponse
import com.stapp.sporttrack.data.models.ExerciseStatsUnit
import com.stapp.sporttrack.data.models.ExerciseSummaryCardData
import com.stapp.sporttrack.data.repository.ExerciseRepository
import com.stapp.sporttrack.ui.components.ErrorFullScreen
import com.stapp.sporttrack.ui.components.LoadingFullScreen
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.utils.SharedPreferencesConstants
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import com.stapp.sporttrack.viewmodel.ExerciseViewModelFactory
import com.stapp.sporttrack.viewmodel.toFormattedTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun ExerciseSessionDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    exerciseViewModel: ExerciseViewModel,
    context: Context
) {
    val configuration = LocalConfiguration.current
    val isLoading by exerciseViewModel.isLoading.collectAsStateWithLifecycle()

    val addExerciseResult by exerciseViewModel.addExerciseResult.observeAsState()
    val exerciseSession by exerciseViewModel.exerciseSession.collectAsStateWithLifecycle()

    val summaryCardsData = exerciseSession?.let { generateSummaryCardsData(it) }
    val exerciseStrategy =
        exerciseSession?.let { ExerciseUtils.getExerciseStrategy(it.activityTypeId) }

    LaunchedEffect(addExerciseResult) {
        addExerciseResult?.let { result ->
            result.onSuccess {
                Toast.makeText(
                    context,
                    "Session d'exercice ajoutée avec succès !",
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure { exception ->
                Toast.makeText(
                    context,
                    exception.message ?: "Erreur lors de l'ajout de la session",
                    Toast.LENGTH_SHORT
                ).show()
            }

            exerciseViewModel.resetAddExerciseResult()
        }
    }

    if (isLoading && exerciseSession != null && exerciseStrategy != null) {
        LoadingFullScreen(modifier)
    } else if (exerciseSession != null && exerciseStrategy != null) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .imePadding()
                .fillMaxSize()
        ) {
            item {
                ExerciseDetailHeader(
                    onBackClick = onBackClick,
                    duration = toFormattedTime(exerciseSession!!.duration!!),
                    title = exerciseStrategy.getTitle(),
                    bgImage = exerciseStrategy.getImage(),
                    icon = exerciseStrategy.getIcon(),
                    height = configuration.screenHeightDp.dp / 3,
                    date = formatDateTimeRange(
                        exerciseSession?.startDate,
                        exerciseSession?.endDate
                    )
                )
            }

            exerciseSession?.let {
                item {
                    Column(modifier = modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = CardDefaults.cardColors().containerColor.copy(
                                    0.3f
                                )
                            )
                        ) {
                            Column(modifier = Modifier.padding(15.dp)) {
                                Spacer(Modifier.height(10.dp))
                                ExerciseSummaryGrid(
                                    configuration = configuration,
                                    summaryCardsData = summaryCardsData!!
                                )
                            }
                        }
                    }
                }
            }

        }
    } else {
        ErrorFullScreen(modifier)
    }

}

@Composable
fun ExerciseSummaryGrid(
    summaryCardsData: List<ExerciseSummaryCardData>,
    configuration: Configuration

) {
    for (i in summaryCardsData.indices step 2) {
        Row(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(),
        ) {
            ExerciseDetailItem(
                summaryCardsData[i],
                configuration.screenWidthDp.dp / 2
            )
            if (i + 1 < summaryCardsData.size) {
                ExerciseDetailItem(
                    summaryCardsData[i + 1],
                    configuration.screenWidthDp.dp / 2,
                    MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun ExerciseDetailHeader(
    duration: String,
    date: String,
    title: Int,
    bgImage: Int,
    icon: Int,
    height: Dp,
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-15).dp)
            .height(height)
    ) {
        Image(
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds,
            painter = painterResource(id = bgImage),
            contentDescription = stringResource(title),
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        ),

                        )
                )
        )
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 50.dp, start = 16.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                modifier = Modifier.size(30.dp)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                tint = MaterialTheme.colorScheme.surface,
                contentDescription = stringResource(title),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(title),
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = date,
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }

    }
}


@Composable
fun ExerciseDetailItem(
    summaryCardData: ExerciseSummaryCardData,
    width: Dp,
    valueColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = Modifier.width(width)
    ) {
        Text(
            summaryCardData.title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
        Text(
            "${summaryCardData.value} ${summaryCardData.unit ?: ""}",
            modifier = Modifier.padding(bottom = 5.dp),
            color = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize
        )
    }
}


fun generateSummaryCardsData(exerciseSession: ExerciseSessionResponse?): List<ExerciseSummaryCardData> {
    val summaryCardsData = mutableListOf<ExerciseSummaryCardData>()

    if (exerciseSession != null) {

        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Distance",
                value = exerciseSession.distance.toString(),
                unit = ExerciseStatsUnit.DISTANCE
            )
        )


        if (exerciseSession.averageSpeed != 0.0) {
            summaryCardsData.add(
                ExerciseSummaryCardData(
                    title = "Vitesse moyenne",
                    value = exerciseSession.averageSpeed.toString(),
                    unit = ExerciseStatsUnit.SPEED
                )
            )
        }

        if (exerciseSession.stepCount != 0) {
            summaryCardsData.add(
                ExerciseSummaryCardData(
                    title = "Pas",
                    value = exerciseSession.stepCount.toString()
                )
            )
        }

        if (exerciseSession.cadence != 0.0) {
            summaryCardsData.add(
                ExerciseSummaryCardData(
                    title = "Cadence",
                    value = exerciseSession.cadence.toString(),
                    unit = ExerciseStatsUnit.CADENCE
                )
            )
        }

        if (exerciseSession.rhythm != 0.0) {
            summaryCardsData.add(
                ExerciseSummaryCardData(
                    title = "Rythme",
                    value = exerciseSession.rhythm.toString(),
                    unit = ExerciseStatsUnit.RHYTHM
                )
            )
        }

        if (exerciseSession.slope != 0.0) {
            summaryCardsData.add(
                ExerciseSummaryCardData(
                    title = "Pente",
                    value = exerciseSession.slope.toString(),
                    unit = ExerciseStatsUnit.SLOPE
                )
            )
        }

        summaryCardsData.add(
            ExerciseSummaryCardData(
                title = "Calories brûlées",
                value = exerciseSession.caloriesBurned.toString(),
                unit = ExerciseStatsUnit.CALORIES
            )
        )

    }
    return summaryCardsData
}

fun formatDateTimeRange(start: LocalDateTime?, end: LocalDateTime?): String {
    if (start == null || end == null) return ""

    val dayFormatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.FRENCH)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH)

    return if (start.toLocalDate() == end.toLocalDate()) {

        "${start.format(dayFormatter)} ${start.format(timeFormatter)} - ${end.format(timeFormatter)}"
    } else {

        "${start.format(dayFormatter)} ${start.format(timeFormatter)} à ${end.format(dayFormatter)} ${
            end.format(
                timeFormatter
            )
        }"
    }
}

@Preview(showBackground = true)
@Composable
fun WalkingDetailPreview() {
    SportTrackTheme {
        val context = LocalContext.current
        val viewModelStoreOwner = LocalViewModelStoreOwner.current

        val sharedPreferences =
            context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, MODE_PRIVATE)
        val exerciseRepository = ExerciseRepository(sharedPreferences)
        val exerciseViewModel = viewModelStoreOwner?.let {
            ViewModelProvider(
                it,
                ExerciseViewModelFactory(exerciseRepository)
            )
        }?.get(ExerciseViewModel::class.java)

        Scaffold { paddingValues ->
            if (exerciseViewModel != null) {
                ExerciseSessionDetailScreen(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(top = 16.dp)
                        .padding(horizontal = 10.dp),
                    onBackClick = {

                    },
                    exerciseViewModel = exerciseViewModel,
                    context = LocalContext.current
                )
            }
        }

    }
}

