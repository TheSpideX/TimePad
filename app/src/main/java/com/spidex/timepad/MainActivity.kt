package com.spidex.timepad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.spidex.timepad.ui.theme.TimePadTheme
import com.spidex.timepad.ui.theme.background

class MainActivity : ComponentActivity() {
    val task = Task(
        id = 0,
        title = "Project",
        durationMinutes = 1,
        tag = "Workout",
        icon = R.drawable.ic_workout,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TimePadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}