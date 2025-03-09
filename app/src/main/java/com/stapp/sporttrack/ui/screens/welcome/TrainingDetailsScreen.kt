package com.stapp.sporttrack.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.stapp.sporttrack.data.models.ExerciseSessionResponse
import com.stapp.sporttrack.data.models.ExerciseStatsUnit
import com.stapp.sporttrack.ui.components.DayItem
import com.stapp.sporttrack.ui.components.WeekNavigation
import com.stapp.sporttrack.utils.getDaysOfWeek
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import com.valentinilk.shimmer.shimmer
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Badge
import androidx.compose.ui.text.style.TextAlign
import com.stapp.sporttrack.R
import com.stapp.sporttrack.utils.toFormattedTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun TrainingDetailsScreen(
    navController: NavHostController,
    exerciseViewModel: ExerciseViewModel,
    modifier: Modifier = Modifier
) {
    val trainingHistory by exerciseViewModel.exerciseSessions.collectAsState()
    val isLoading by exerciseViewModel.isLoading.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        exerciseViewModel.loadExerciseSessions()
    }

    val sortedTrainingHistory = trainingHistory.toSortedMap(compareByDescending { it })
    val dateList = sortedTrainingHistory.keys.toList()

    val currentDateFlow = remember { MutableStateFlow(dateList.firstOrNull() ?: LocalDate.now()) }
    val currentDate by currentDateFlow.collectAsState()

    val listState = rememberLazyListState()

    fun onDateSelected(date: LocalDate) {
        currentDateFlow.value = date
        scope.launch {
            val index = dateList.indexOf(date)
            if (index >= 0) {
                listState.animateScrollToItem(index)
            }
        }
    }

    val baseDate = remember { LocalDate.now() }

    val initialPage = remember {
        val basePage = Int.MAX_VALUE / 2
        val weekDifference = ChronoUnit.WEEKS.between(
            baseDate,
            currentDate.with(DayOfWeek.MONDAY)
        ).toInt()
        basePage + weekDifference
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )

    Column(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints {
            val calculatedHeight = maxHeight / 5

            DateWeekSelector(
                trainingDates = dateList.toSet(),
                currentDate = currentDate,
                pagerState = pagerState,
                baseDate = baseDate,
                initialPage = initialPage,
                onDateSelected = { date -> onDateSelected(date) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calculatedHeight.coerceAtLeast(0.dp))
            )
        }

        TrainingListView(
            exerciseViewModel = exerciseViewModel,
            trainingHistory = trainingHistory,
            dateList = dateList,
            listState = listState,
            navController = navController,
            onVisibleDateChanged = { date ->
                currentDateFlow.value = date
            },
            isLoading = isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(
                    MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        )
    }
}

@Composable
fun TrainingListView(
    trainingHistory: Map<LocalDate, List<ExerciseSessionResponse>>,
    dateList: List<LocalDate>,
    listState: LazyListState,
    navController: NavController,
    onVisibleDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    exerciseViewModel: ExerciseViewModel,
    isLoading: Boolean
) {
    val shimmerModifier = if (isLoading) {
        Modifier.shimmer()
    } else {
        Modifier
    }
    when{
        isLoading ->{
        LazyColumn(
            state = listState,
            modifier = modifier
        ) {
            loadingShimmerLayout(shimmerModifier)
        }
        }
        dateList.isNotEmpty()->{
            LazyColumn(
                state = listState,
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    ),
            ) {
                itemsIndexed(dateList) { _, date ->
                    val sessions = trainingHistory[date] ?: emptyList()
                    val totalDuration = sessions.sumOf { it.duration ?: 0 }
                    val totalCaloriesBurnedRecord =
                        sessions.sumOf { it.caloriesBurned ?: 0.0 }
                    val formattedTotalDuration = toFormattedTime(totalDuration)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = CardDefaults.cardColors().containerColor.copy(0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ){
                            val sortedSessions = sessions.sortedByDescending { it.startDate }

                            DailyDetailHeader(date, formattedTotalDuration, totalCaloriesBurnedRecord)

                            sortedSessions.forEach { session ->
                                TrainingItem(session) {
                                    exerciseViewModel.loadExerciseSessionById(session.sessionId)
                                    navController.navigate("${Screen.ExerciseSessionDetail.route}/back")
                                }
                            }
                        }
                    }
                }
            }
            LaunchedEffect(listState) {
                snapshotFlow { listState.firstVisibleItemIndex }
                    .distinctUntilChanged()
                    .collect { index ->
                        val visibleDate = dateList.getOrNull(index)
                        if (visibleDate != null) {
                            onVisibleDateChanged(visibleDate)
                        }
                    }
            }
        }
            else ->{
                Column(
                    modifier = modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.training_history),
                        contentDescription = "Training History",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(50.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Aucun historique d'entraînements pour le moment",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
    }

}

private fun LazyListScope.loadingShimmerLayout(modifier: Modifier){
    items(5) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardDefaults.cardColors().containerColor.copy(0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                ShimmerDailyDetailHeader()
                ShimmerTrainingItem()
                ShimmerTrainingItem()
                ShimmerTrainingItem()
            }
        }
    }
}

@Composable
fun ShimmerTrainingItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(0.5f)
        )
    ) {
    }
}

@Composable
fun ShimmerDailyDetailHeader() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .padding(8.dp),
    ) {
    }
}


@Composable
fun DailyDetailHeader(
    date: LocalDate,
    formattedTotalDuration: String,
    totalCaloriesBurnedRecord: Double
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEE dd MMM yyy")),
            style = MaterialTheme.typography.titleSmall,
        )
        Row {
            Text(
                text = formattedTotalDuration,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = " | ",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${
                    String.format(
                        Locale.getDefault(),
                        "%.2f",
                        totalCaloriesBurnedRecord
                    )
                } ${ExerciseStatsUnit.CALORIES}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                ),
            )
        }
    }
}

@Composable
fun TrainingItem(session: ExerciseSessionResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = CardDefaults.cardColors().containerColor.copy(0.5f)
        )
    ) {
        val exercise = ExerciseUtils.getExerciseStrategy(session.activityTypeId)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(exercise.getIcon()),
                contentDescription = stringResource(exercise.getTitle()),
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp),
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                Text(
                    text = stringResource(exercise.getTitle()),
                    style = MaterialTheme.typography.titleMedium
                )
                 if(session.isAuto){
                     Badge {
                         Text(text = "Auto")
                     }
                 }
                }
                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = session.duration?.let { toFormattedTime(it) } ?: "",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ))
                    Text(
                        text = session.startDate.format(DateTimeFormatter.ofPattern("HH:mm")),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
fun DateWeekSelector(
    trainingDates: Set<LocalDate>,
    currentDate: LocalDate,
    pagerState: PagerState,
    baseDate: LocalDate,
    initialPage: Int,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentDate) {
        val weekDifference = ChronoUnit.WEEKS.between(
            baseDate,
            currentDate.with(DayOfWeek.MONDAY)
        ).toInt()
        val targetPage = initialPage + weekDifference

        if (pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    Column(modifier = modifier) {

        WeekNavigation(
            currentWeekStart = baseDate.plusWeeks((pagerState.currentPage - initialPage).toLong()),
            onPreviousWeek = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNextWeek = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val weekOffset = page - initialPage
            val weekStart = baseDate.plusWeeks(weekOffset.toLong())

            val daysOfWeek = getDaysOfWeek(weekStart, trainingDates)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(daysOfWeek) { day ->
                    DayItem(
                        day = day,
                        isSelected = day.date == currentDate,
                        onClick = {
                            onDateSelected(day.date)
                        }
                    )
                }
            }
        }
    }
}
