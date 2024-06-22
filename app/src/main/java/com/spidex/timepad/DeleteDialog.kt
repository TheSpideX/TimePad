package com.spidex.timepad

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
fun DeleteDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
){
    var deletePermanently by remember {
        mutableStateOf(false)
    }
    val currentTask by viewModel.currentTask.collectAsState()
    val task by viewModel.deleteTask.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp)
            ){
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = if(task?.repeatInterval!=RepeatInterval.NONE) {
                                    if (deletePermanently) "Delete"
                                    else "Shift"
                                }
                                else{
                                    "Delete"
                                },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text =
                            if(task?.repeatInterval!=RepeatInterval.NONE) {
                                if (deletePermanently) "Task will be Deleted Permanently"
                                else "Task will be Shifted to Next Date"
                            }
                            else{
                                "Task will be Deleted Permanently"
                            },
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TaskViewForDelete(viewModel = viewModel, task = task!!, deletePermanently){
                        deletePermanently = !deletePermanently
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if(task?.repeatInterval == RepeatInterval.NONE){
                                    if(currentTask == task){
                                        viewModel.setCurrentTask(null)
                                    }
                                    if(task!=null)
                                        viewModel.deleteTask(task!!)
                                }
                                else
                                {
                                    if(currentTask == task){
                                        viewModel.setCurrentTask(null)
                                    }
                                    if(deletePermanently){
                                        if(task!=null)
                                            viewModel.deleteTask(task!!)
                                    }
                                    else
                                    {
                                        if(task!=null)
                                            viewModel.shiftTaskToNextInterval(task!!)
                                    }
                                }
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20)
                        ) {
                            Text(
                                text = if(task?.repeatInterval != RepeatInterval.NONE) {
                                    if (deletePermanently) "Delete" else "Shift"
                                }
                                else{
                                    "Delete"
                                },
                                modifier = Modifier
                            )
                        }


                        TextButton(onClick = {
                            onDismiss()
                        }) {
                            Text(
                                text = "Cancel",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskViewForDelete(viewModel: TaskViewModel, task : Task,delete : Boolean, onClick : () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20),
        onClick = {
            onClick()
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
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
                    .width(100.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(image.end, margin = 16.dp)
                        width = Dimension.preferredWrapContent
                    }
                    .horizontalScroll(rememberScrollState())
            )

            Card(
                modifier = Modifier
                    .width(32.dp)
                    .height(8.dp)
                    .constrainAs(time) {
                        top.linkTo(parent.top, margin = 16.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    },
                colors = CardDefaults.cardColors(containerColor =
                    if(task.repeatInterval!=RepeatInterval.NONE) {
                        when (delete) {
                            true -> red
                            false -> green
                            else -> Color(0xfFFFA656)
                        }
                    }
                    else{
                        red
                    }
                )
            ){}

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
            )

            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .width(120.dp)
                    .constrainAs(tag) {
                        start.linkTo(image.end, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
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
                        modifier = Modifier.padding(start = 8.dp,end = 8.dp,top = 2.dp,bottom = 2.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DeleteDialogPreview(){
    val date = remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val task = Task(
        id = 0,
        title = "Project",
        durationMinutes = 1,
        tag = "Workout",
        icon = R.drawable.ic_workout,
        repeatInterval = RepeatInterval.NONE
    )
    val context = LocalContext.current
    val taskViewModel = TaskViewModel(taskRepository = TaskRepository(AppDatabase.getDatabase(
        LocalContext.current).taskDao()))
    DeleteDialog(taskViewModel){

    }
}