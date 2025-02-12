package com.stapp.sporttrack.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.stapp.sporttrack.data.models.UserResponse
import kotlinx.serialization.json.Json

fun getUserData(context: Context): UserResponse? {
    val sharedPref =context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, MODE_PRIVATE)
    val userJson =
        sharedPref.getString(SharedPreferencesConstants.USER_DATA, null) ?: return null
    val user = Json.decodeFromString<UserResponse>(userJson)
    return user
}