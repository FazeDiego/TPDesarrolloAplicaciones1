package com.example.safewalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.safewalk.navigation.AppNavigation
import com.example.safewalk.ui.theme.SafeWalkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SafeWalkTheme {
                AppNavigation()


            }
        }
    }
}
