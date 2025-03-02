package com.stapp.sporttrack.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stapp.sporttrack.data.repository.AuthRepository

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val context: Context, private val sharedPreferences: SharedPreferences) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            AuthViewModel(context,AuthRepository(sharedPreferences)) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
