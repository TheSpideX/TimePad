package com.spidex.timepad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.spidex.timepad.data.AppDatabase
import com.spidex.timepad.data.TaskRepository
import com.spidex.timepad.ui.theme.TimePadTheme
import com.spidex.timepad.ui.theme.background
import com.spidex.timepad.viewModel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TimePadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = background
                ) {
                    val soundHelper = SoundHelper(LocalContext.current)
                    val viewModel = TaskViewModel(taskRepository = TaskRepository(
                        AppDatabase.getDatabase(
                        LocalContext.current).taskDao()),
                        soundHelper
                    )

                    AppNavigation(viewModel)
                }
            }
        }
    }
}