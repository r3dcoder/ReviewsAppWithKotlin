package com.example.vollysql


import android.content.Context

object Utils {
    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        return prefs.getBoolean("isLoggedIn", false)
    }
}
