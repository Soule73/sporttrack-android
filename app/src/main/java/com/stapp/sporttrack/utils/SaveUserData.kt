package com.stapp.sporttrack.utils

import android.content.Context
import com.stapp.sporttrack.data.models.UserResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun saveUserData(context: Context, userResponse: UserResponse) {
    val sharedPref =
        context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
    val userJson = Json.encodeToString(userResponse)
    with(sharedPref.edit()) {
        putString(SharedPreferencesConstants.USER_DATA, userJson)
        apply()
    }
}