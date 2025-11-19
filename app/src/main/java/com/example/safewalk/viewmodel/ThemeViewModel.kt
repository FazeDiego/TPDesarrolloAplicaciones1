package com.example.safewalk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun loadTheme(context: Context) {
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        _isDarkTheme.value = prefs.getBoolean("useDarkTheme", false)
    }

    fun toggleTheme(context: Context) {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue

        // Guardar en SharedPreferences
        val prefs = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("useDarkTheme", newValue).apply()
    }
}

