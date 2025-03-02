package com.stapp.sporttrack.utils

import android.content.Context
import com.stapp.sporttrack.data.models.AuthResponse
import com.stapp.sporttrack.data.models.UserResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AuthUtils {
    fun checkAuthentication(context: Context): Boolean {
        try {
            val sharedPref =
                context.getSharedPreferences(
                    SharedPreferencesConstants.PREF_NAME,
                    Context.MODE_PRIVATE
                )
            val token = sharedPref.getString(SharedPreferencesConstants.AUTH_TOKEN, null)

            return token != null
        } catch (e: Exception) {
            return false
        }
    }

    fun checkIfIsFirstOpen(context: Context): Boolean {
        try {
            val sharedPref =
                context.getSharedPreferences(
                    SharedPreferencesConstants.PREF_NAME,
                    Context.MODE_PRIVATE
                )

            val isFirstOpen = sharedPref.getBoolean(SharedPreferencesConstants.IS_FIRST_OPEN, true)
            return isFirstOpen
        } catch (e: Exception) {
            return false
        }
    }
    fun getUserData(context: Context): UserResponse? {
        val sharedPref = context.getSharedPreferences(
            SharedPreferencesConstants.PREF_NAME,
            Context.MODE_PRIVATE
        )
        val userJson =
            sharedPref.getString(SharedPreferencesConstants.USER_DATA, null) ?: return null
        val user = Json.decodeFromString<UserResponse>(userJson)
        return user
    }

    fun saveUserData(context: Context, userResponse: UserResponse) {
        val sharedPref =
            context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
        val userJson = Json.encodeToString(userResponse)
        with(sharedPref.edit()) {
            putString(SharedPreferencesConstants.USER_DATA, userJson)
            apply()
        }
    }

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
}

