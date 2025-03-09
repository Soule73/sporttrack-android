package com.stapp.sporttrack.ui.screens.exercise

//import androidx.compose.ui.tooling.preview.Preview
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.navigation.Screen
import com.stapp.sporttrack.ui.theme.SportTrackTheme
import com.stapp.sporttrack.utils.ExerciseUtils
import com.stapp.sporttrack.utils.SharedPreferencesHelper

/**
 * Shows a list of [ExerciseSessionRecord]s from today.
 */
@Composable
fun ExerciseListScreen(
    context: Context,
    navController: NavHostController,
    modifier: Modifier,
) {

    var favoriteExercises by remember {
        mutableStateOf(
            SharedPreferencesHelper.getFavoriteExercises(
                context
            )
        )
    }

    val exercises = listOf(
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING,
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
        ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL,
        ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING,
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (favoriteExercises.isNotEmpty()) {
            item {
                Text(
                    "Favoris",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 15.dp)
                        .fillMaxWidth()
                )
            }

            val favoriteFilteredExercises = exercises.filter { it in favoriteExercises }

            items(favoriteFilteredExercises) { exerciseType ->
                val index = favoriteFilteredExercises.indexOf(exerciseType)
                val isSingleItem = favoriteFilteredExercises.size == 1
                val isFirst = isSingleItem || index == 0
                val isLast = isSingleItem || index == favoriteFilteredExercises.size - 1
                val exerciseStrategy = ExerciseUtils.getExerciseStrategy(exerciseType)
                val icon = exerciseStrategy.getIcon()
                val title = stringResource(exerciseStrategy.getTitle())

                CustomListItem(
                    title = title,
                    isFirst = isFirst,
                    isLast = isLast,
                    onClick = {
                        navController.navigate(Screen.ExerciseSession.route + "/$exerciseType")
                    },
                    prefixIcon = {
                        Icon(
                            painter = painterResource(
                                icon,
                            ), contentDescription = title,
                            modifier = Modifier
                                .size(50.dp)
                                .padding(5.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    endIcon = {
                        Icon(Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = Color.Yellow,
                            modifier = Modifier
                                .size(35.dp)
                                .clickable {
                                    favoriteExercises = favoriteExercises - exerciseType
                                    SharedPreferencesHelper.setFavoriteExercises(
                                        context,
                                        favoriteExercises
                                    )
                                })
                    }

                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Autres exercices",
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 15.dp)
                    .fillMaxWidth()
            )
        }

        val nonFavoriteFilteredExercises = exercises.filter { it !in favoriteExercises }

        items(nonFavoriteFilteredExercises) { exerciseType ->
            val index = nonFavoriteFilteredExercises.indexOf(exerciseType)
            val exerciseStrategy = ExerciseUtils.getExerciseStrategy(exerciseType)
            val icon = exerciseStrategy.getIcon()
            val title = stringResource(exerciseStrategy.getTitle())
            CustomListItem(
                title = title,
                isFirst = index == 0,
                isLast = index == nonFavoriteFilteredExercises.size - 1,
                onClick = {
                    navController.navigate(Screen.ExerciseSession.route + "/$exerciseType")
                },
                prefixIcon = {
                    Icon(
                        painter = painterResource(
                            icon,
                        ), contentDescription = title,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(5.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                },
                endIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_outline_24),
                        contentDescription = "Non Favorite",
                        tint =MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(35.dp)
                            .clickable {
                                favoriteExercises = favoriteExercises + exerciseType
                                SharedPreferencesHelper.setFavoriteExercises(
                                    context,
                                    favoriteExercises
                                )
                            })

                }
            )
        }
    }
}

@Composable
fun CustomListItem(
    title: String,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onClick: () -> Unit = {},
    endIcon: @Composable () -> Unit = {},
    prefixIcon: @Composable () -> Unit = {},
    color: Color? = null

) {
    val shape = if (isFirst && isLast) {
        RoundedCornerShape(15.dp)
    } else if (isFirst) {
        RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
    } else if (isLast) {
        RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
    } else {
        RectangleShape
    }
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = color?.copy(0.3f)
                ?: CardDefaults.cardColors().containerColor.copy(0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                prefixIcon()
                Text(
                    title, fontWeight = FontWeight.SemiBold,
                    color = color ?: Color.Unspecified,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            endIcon()
        }
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 50.dp, end = 20.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}


@Preview(
    showBackground = true
)
@Composable
fun ExerciseSessionScreenPreview() {
    SportTrackTheme {
        val modifier = Modifier
        ExerciseListScreen(
            context = LocalContext.current,
            modifier = modifier,
            navController = rememberNavController()
        )
    }
}
