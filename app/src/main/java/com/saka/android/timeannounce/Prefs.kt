package com.saka.android.timeannounce

import android.content.Context
import android.content.SharedPreferences

object Prefs {

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    fun setState(state: Boolean) {
        preferences.edit().putBoolean(STATE, state).apply()
    }

    fun getState(): Boolean {
        return preferences.getBoolean(STATE, false)
    }

    fun setGreeting(greeting: String) {
        preferences.edit().putString(GREETING, greeting).apply()
    }

    fun getGreeting(): String {
        return preferences.getString(GREETING, "") ?: ""
    }

    private const val STATE = "state"
    private const val GREETING = "greeting"
}