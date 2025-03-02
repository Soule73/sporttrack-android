package com.stapp.sporttrack.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.stapp.sporttrack.ui.components.ErrorFullScreen
import com.stapp.sporttrack.ui.components.LoadingFullScreen
import com.stapp.sporttrack.ui.components.WeekNavigation
import com.stapp.sporttrack.utils.getDaysOfWeek
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.viewmodel.ExerciseViewModel
import com.stapp.sporttrack.viewmodel.toFormattedTime
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
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        exerciseViewModel.loadExerciseSessions()
    }

    val sortedTrainingHistory = trainingHistory.toSortedMap(compareByDescending { it })
    val dateList = sortedTrainingHistory.keys.toList()

    val isDataLoaded = dateList.isNotEmpty()

    val currentDateFlow = remember { MutableStateFlow(dateList.firstOrNull() ?: LocalDate.now()) }
    val currentDate by currentDateFlow.collectAsState()

    val listState = rememberLazyListState()

    val isLoading by exerciseViewModel.isLoading.collectAsStateWithLifecycle()

    fun onDateSelected(date: LocalDate) {
        currentDateFlow.value = date
        scope.launch {
            val index = dateList.indexOf(date)

            if (index >= 0) {
                listState.animateScrollToItem(index)
            }
        }
    }
    if (isLoading && !isDataLoaded) {
        LoadingFullScreen(modifier)
    } else {
        if (isDataLoaded) {

            val baseDate = remember { LocalDate.now().with(DayOfWeek.MONDAY) }
            val configuration = LocalConfiguration.current

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
                DateWeekSelector(
                    trainingDates = dateList.toSet(),
                    currentDate = currentDate,
                    pagerState = pagerState,
                    baseDate = baseDate,
                    initialPage = initialPage,
                    onDateSelected = { date -> onDateSelected(date) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(configuration.screenHeightDp.dp / 5)
                )

                TrainingListView(
                    exerciseViewModel = exerciseViewModel,
                    trainingHistory = trainingHistory,
                    dateList = dateList,
                    listState = listState,
                    navController = navController,
                    onVisibleDateChanged = { date ->
                        currentDateFlow.value = date
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        } else {
            ErrorFullScreen(modifier)
        }
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
    exerciseViewModel: ExerciseViewModel
) {

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
                ) {
                    DailyDetailHeader(
                        date = date,
                        formattedTotalDuration = formattedTotalDuration,
                        totalCaloriesBurnedRecord = totalCaloriesBurnedRecord
                    )

                    sessions.forEach { session ->
                        TrainingItem(session) {
                            exerciseViewModel.loadExerciseSessionById(session.sessionId)

                            navController.navigate(Screen.ExerciseSessionDetail.route)
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

                println("Date visible : $visibleDate , Index : $index")
                if (visibleDate != null) {
                    onVisibleDateChanged(visibleDate)
                }
            }
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
                Text(
                    text = stringResource(exercise.getTitle()),
                    style = MaterialTheme.typography.titleMedium
                )
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
                horizontalArrangement = Arrangement.spacedBy(2.dp)
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
