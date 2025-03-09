
package com.stapp.sporttrack.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.maps.model.LatLng
import com.stapp.sporttrack.data.models.ExerciseStatsUnit
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Shows details of a given throwable in the snackbar
 */
//fun displayError(
//  scaffoldState: SnackbarHostState,
//  scope: CoroutineScope,
//  throwable: Throwable?,
//) {
//  scope.launch {
//    scaffoldState.showSnackbar(
//      message = throwable?.localizedMessage ?: "Unknown exception",
//      duration = SnackbarDuration.Short
//    )
//  }
//}

@SuppressLint("ServiceCast")
fun hideKeyboard(context: Context) {
   val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
   val view = (context as Activity).currentFocus
   view?.let {
       imm.hideSoftInputFromWindow(it.windowToken, 0)
   }
}

object SharedPreferencesConstants {
    const val PREF_LIGHT_MODE= "PREF_LIGHT_MODE"
    const val PREF_SYSTEM = "PREF_SYSTEM"
    const val PREF_DARK_MODE = "PREF_DARK_MODE"
    const val PREF_NAME = "AppPreferences"
    const val AUTH_TOKEN = "AUTH_TOKEN"
    const val USER_DATA = "USER_DATA"
    const val IS_FIRST_OPEN = "IS_FIRST_OPEN"
    const val FAVORITES_KEY = "FAVORITES_EXERCISES"
    const val AUTO_PAUSE_ENABLED = "AUTO_PAUSE_ENABLED"

}


fun LatLng.distanceTo(destination: LatLng?): Double {
    if (destination != null) {
        val result = FloatArray(1)
      Location.distanceBetween(
        this.latitude,
        this.longitude,
        destination.latitude,
        destination.longitude,
        result
      )
        return result[0].toDouble()
    }
    return 0.0
}

fun String.capitalizeFirstChar(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun formatWeeklyStartEndDate(currentWeekStart: LocalDate):String{
    return "${currentWeekStart.format(DateTimeFormatter.ofPattern("MMM dd"))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} - ${
        currentWeekStart.plusDays(6).format(DateTimeFormatter.ofPattern("MMM dd"))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }"
}

fun getDaysOfWeek(date: LocalDate, trainingDates: Set<LocalDate>): List<Day> {
    val startOfWeek = date.with(DayOfWeek.MONDAY)
    return (0..6).map { offset ->
        val dayDate = startOfWeek.plusDays(offset.toLong())
        Day(
            date = dayDate,
            hasTraining = trainingDates.contains(dayDate)
        )
    }
}

data class Day(
    val date: LocalDate,
    val hasTraining: Boolean
)

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertMillisToDateFr(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertDateToMillis(dateString: String): Long {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.parse(dateString)?.time ?: throw IllegalArgumentException("Invalid date format")
}

fun toSeconds(formattedTime: String): Int {
    val parts = formattedTime.split(":")
    val minutes = parts[0].toInt()
    val seconds = parts[1].toInt()
    return minutes * 60 + seconds
}

fun toFormattedTime(seconds: Int): String {
    val formattedMinutes = seconds / 60
    val formattedSeconds = seconds % 60

    var minAndSeconds=""

    if(formattedMinutes > 0){
        minAndSeconds = "$formattedMinutes "+ ExerciseStatsUnit.MIN
    }
    if(formattedSeconds > 0){
        minAndSeconds += " $formattedSeconds "+ ExerciseStatsUnit.SEC
    }

    return minAndSeconds
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