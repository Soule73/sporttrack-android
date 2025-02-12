package com.stapp.sporttrack.utils

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import com.stapp.sporttrack.ui.screens.WelcomeActivity
import com.stapp.sporttrack.ui.screens.auth.RegisterActivity

fun checkAuthentication(context: Context, checkIfIsFirstOpen: Boolean = false): Boolean {
    val sharedPref =
        context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
    val token = sharedPref.getString(SharedPreferencesConstants.AUTH_TOKEN, null)

    if (token != null) {
        val intent = Intent(context, WelcomeActivity::class.java)
        context.startActivity(intent)
        (context as ComponentActivity).finish()
        return true
    }

    if (checkIfIsFirstOpen) {
        val isFirstOpen = sharedPref.getBoolean(SharedPreferencesConstants.IS_FIRST_OPEN, true)
        if (isFirstOpen) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
            (context as ComponentActivity).finish()
            return true
        }
    }

    return false
}
