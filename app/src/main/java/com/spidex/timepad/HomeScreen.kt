package com.spidex.timepad

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.exyte.animatednavbar.utils.noRippleClickable
import com.spidex.timepad.ui.theme.background
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


@Composable
fun HomeScreen(viewModel: TaskViewModel, context : Context, navigateToClock: () -> Unit){

    val taskList by viewModel.todayTasks.collectAsState()
    val currentTask by viewModel.currentTask.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val deleteDialog by viewModel.showDeleteDialog.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(background)
    ){

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = background)
                .padding(top = 16.dp),
        ){
            Text(
                text = "Task",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12),
                elevation = CardDefaults.cardElevation(16.dp),
                onClick = {
                    if(currentTask != null){
                        viewModel.startOrResumeTimer()
                        navigateToClock()
                    }
                    else
                    {
                        Toast.makeText(context,"No Task is Selected",Toast.LENGTH_SHORT).show()
                    }
                }
            ){
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val (name,time,arrow) = createRefs()
                    val remTime = currentTask?.remainingTimeMillis ?: 0
                    var hour = (remTime / 3600000).toString()
                    if(hour.length == 1)
                    {
                        hour = "0$hour"
                    }
                    var min = ((remTime / 60000) % 60).toString()
                    if(min.length == 1)
                    {
                        min = "0$min"
                    }
                    var sec = ((remTime / 1000) % 60).toString()
                    if(sec.length == 1)
                    {
                        sec = "0$sec"
                    }
                    Text(
                        text = "${hour}:${min}:${sec}",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .constrainAs(time){
                                top.linkTo(parent.top,margin = 16.dp)
                                start.linkTo(parent.start,margin = 24.dp)
                                bottom.linkTo(name.top)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = null,
                        modifier = Modifier
                            .width(16.dp)
                            .height(16.dp)
                            .constrainAs(arrow) {
                                top.linkTo(parent.top, margin = 28.dp)
                                end.linkTo(parent.end, margin = 24.dp)
                            }
                        ,
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .constrainAs(name) {
                                bottom.linkTo(parent.bottom, margin = 16.dp)
                                start.linkTo(parent.start, margin = 32.dp)
                                top.linkTo(time.bottom, margin = 8.dp)
                            }
                    ){

                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = null,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = currentTask?.title ?: "No Task",
                            color = Color.White,
                            fontSize = 18.sp
                        )

                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = "Today",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(taskList) { task ->
                    TaskView(viewModel,task, navigateToClock)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

        }

        if(showDialog){
            InfoDialog(
                viewModel = viewModel ,
                selectedDay = null,
                context = context,
                onDismiss = {
                    viewModel.setShowDialog(false)
                    viewModel.doneEditing()
                            },
                onTaskSaved = {
                    viewModel.insertTask(it)
                    viewModel.setShowDialog(false)
                    viewModel.doneEditing()
                },
                onTaskUpdate = {
                    viewModel.updateTask(it)
                    viewModel.setShowDialog(false)
                    viewModel.doneEditing()

                }
            )
        }

        if(deleteDialog){
            DeleteDialog(viewModel = viewModel) {
                viewModel.setShowDeleteDialog(false)
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskView(viewModel: TaskViewModel,task : Task,navigateToClock: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .combinedClickable(
                    onClick = {
                        viewModel.setCurrentTask(task)
                        viewModel.startOrResumeTimer()
                        navigateToClock()
                    },
                    onDoubleClick = {
                        viewModel.setEditTask(task)
                        viewModel.setShowDialog(true)
                    },
                    onLongClick = {
                        viewModel.setDeleteTask(task)
                        viewModel.setShowDeleteDialog(true)
                    }
                )
        ) {
            val (image, title, tag, time, play) = createRefs()
            Image(
                painter = painterResource(id = task.icon ?: R.drawable.ic_personal),
                contentDescription = null,
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .padding(2.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, margin = 12.dp)
                    },
            )
            Text(
                text = task.title,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .width(160.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(image.end, margin = 16.dp)
                        width = Dimension.preferredWrapContent
                    }
                    .horizontalScroll(rememberScrollState())
            )

            val remTime = task.remainingTimeMillis
            var hour = (remTime / 3600000).toString()
            if(hour.length == 1)
            {
                hour = "0$hour"
            }
            var min = (remTime / 60000 % 60).toString()
            if(min.length == 1)
            {
                min = "0$min"
            }
            var sec = ((remTime / 1000) % 60).toString()
            if(sec.length == 1)
            {
                sec = "0$sec"
            }

            Text(
                text = "${hour}:${min}:${sec}",
                modifier = Modifier.constrainAs(time) {
                    top.linkTo(parent.top, margin = 16.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                }
            )

            Image(
                painter = painterResource(id = R.drawable.play),
                contentDescription = null,
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp)
                    .constrainAs(play) {
                        end.linkTo(parent.end, margin = 20.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    }
                    .noRippleClickable {
                        viewModel.setCurrentTask(task)
                        viewModel.startOrResumeTimer()
                        navigateToClock()
                    }
            )

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .constrainAs(tag) {
                        start.linkTo(image.end, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 20.dp)
                    }
                    .background(shape = RoundedCornerShape(20), color = Color.White),
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 12.dp)
                        .background(
                            color = when (task.tag) {
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
                        text = task.tag ?: "Work",
                        color = when(task.tag){
                            "Work" -> red
                            "Coding" -> red
                            "Workout" -> orange
                            "Reading" -> green
                            "Project" -> purple
                            else -> silver
                        },
                        letterSpacing = 2.sp,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp,end = 8.dp,top = 4.dp,bottom = 4.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    val taskViewModel = TaskViewModel(taskRepository = TaskRepository(AppDatabase.getDatabase(
        LocalContext.current).taskDao())
    )
    val task = Task(
        id = 0,
        title = "Project",
        durationMinutes = 1,
        tag = "Workout",
        icon = R.drawable.ic_workout,
    )
    TaskView(viewModel = taskViewModel, task = task) {
        
    }
}