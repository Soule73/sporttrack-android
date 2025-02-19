package com.stapp.sporttrack.utils

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import com.stapp.sporttrack.data.models.AuthResponse
import com.stapp.sporttrack.ui.LoginActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun saveUserDataAndToken(context: Context, loginResponse: AuthResponse) {
    val sharedPref =
        context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
    val userJson = Json.encodeToString(loginResponse.user)
    with(sharedPref.edit()) {
        putString(SharedPreferencesConstants.AUTH_TOKEN, loginResponse.token)
        putString(SharedPreferencesConstants.USER_DATA, userJson)
        putBoolean(SharedPreferencesConstants.IS_FIRST_OPEN, false)
        apply()
    }
}

fun navigateToLogin(context: Context, checkAuthentication: Boolean = true) {
    val intent = Intent(context, LoginActivity::class.java).apply {
        putExtra("checkAuthentication", checkAuthentication)
    }
    context.startActivity(intent)
    (context as ComponentActivity).finish()
}