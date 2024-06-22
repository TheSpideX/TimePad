package com.spidex.timepad

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spidex.timepad.ui.theme.green
import com.spidex.timepad.ui.theme.lightGreen
import com.spidex.timepad.ui.theme.lightOrange
import com.spidex.timepad.ui.theme.lightPurple
import com.spidex.timepad.ui.theme.lightRed
import com.spidex.timepad.ui.theme.lightSilver
import com.spidex.timepad.ui.theme.orange
import com.spidex.timepad.ui.theme.purple
import com.spidex.timepad.ui.theme.red
import com.spidex.timepad.ui.theme.silver
import java.time.LocalDate

@Composable
fun TimeScreen(navController: NavController,viewModel: TaskViewModel,context: Context, onFinishClick : (Task) -> Unit){
    val currentTask by viewModel.currentTask.collectAsState()
    currentTask?.let {
        val timerRunning by viewModel.timerRunning.collectAsState()
        BackHandler(onBack = {
            viewModel.pauseTimer()
            navController.popBackStack()
        })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.pauseTimer()
                    navController.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp)
                    )
                }

                Text(
                    text = currentTask?.title ?: "New Task",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 8.dp)
                        .background(
                            color = when (currentTask?.tag ?: "Personal") {
                                "Work" -> lightRed
                                "Coding" -> lightRed
                                "Workout" -> lightOrange
                                "Reading" -> lightGreen
                                "Project" -> lightPurple
                                else -> lightSilver
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                )
                {
                    Text(
                        text = currentTask?.tag ?: "Work",
                        color = when (viewModel.currentTask.value?.tag ?: "Personal") {
                            "Work" -> red
                            "Coding" -> red
                            "Workout" -> orange
                            "Reading" -> green
                            "Project" -> purple
                            else -> silver
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    )
                }
            }

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {

                val (progress, finish, quit, des) = createRefs()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .constrainAs(des) {
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .padding(start = 8.dp, end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentTask?.description ?: "Description",
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier.constrainAs(progress) {
                        top.linkTo(parent.top)
                        bottom.linkTo(finish.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    CircularProgressIndicator(
                        viewModel = viewModel,
                        remainingTimeMillis = currentTask?.remainingTimeMillis ?: 0,
                        totalDurationMillis = currentTask?.durationMinutes ?: 1,
                        progressColor = Color(0xff7012CE),
                        progressBackgroundColor = Color(0xFFe9e9fd),
                        strokeWidth = 16.dp,
                        strokeBackgroundWidth = 20.dp,
                        waveAnimation = false,
                        radius = 140.dp,
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 32.dp, start = 32.dp, end = 32.dp)
                        .constrainAs(finish) {
                            bottom.linkTo(quit.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    shape = RoundedCornerShape(20),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFe9e9fd)),
                    onClick = {
                        viewModel.pauseTimer()
                        currentTask?.let { task ->
                            viewModel.markTaskCompleted(task)
                        }
                        navController.popBackStack()
                        onFinishClick(currentTask!!)
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.pauseTimer()
                                navController.popBackStack()
                                onFinishClick(currentTask!!)
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Finish",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = " Quit ",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            viewModel.pauseTimer()
                            viewModel.getTodayTask()
                            navController.popBackStack()
                        }
                        .constrainAs(quit) {
                            bottom.linkTo(parent.bottom, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(color = Color.White)
        ){

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val taskViewModel = TaskViewModel(taskRepository = TaskRepository(AppDatabase.getDatabase(
        LocalContext.current).taskDao()))
    val context = LocalContext.current
    val navController = rememberNavController()
    TimeScreen(navController,taskViewModel,context){}
}