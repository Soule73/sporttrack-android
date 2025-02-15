package com.stapp.sporttrack.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

@SuppressLint("ServiceCast")
fun hideKeyboard(context: Context) {
   val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
   val view = (context as Activity).currentFocus
   view?.let {
       imm.hideSoftInputFromWindow(it.windowToken, 0)
   }
}