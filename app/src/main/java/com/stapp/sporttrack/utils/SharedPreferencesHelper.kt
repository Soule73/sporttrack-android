package com.stapp.sporttrack.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPreferencesConstants.PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getFavoriteExercises(context: Context): Set<Int> {
        return getPreferences(context).getStringSet(SharedPreferencesConstants.FAVORITES_KEY, emptySet())?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    fun setFavoriteExercises(context: Context, favoriteExercises: Set<Int>) {
        getPreferences(context).edit()
            .putStringSet(SharedPreferencesConstants.FAVORITES_KEY, favoriteExercises.map { it.toString() }.toSet())
            .apply()
    }
}
