package com.spidex.timepad.dataChange

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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.spidex.timepad.R
import com.spidex.timepad.SoundHelper
import com.spidex.timepad.viewModel.TaskViewModel
import com.spidex.timepad.data.AppDatabase
import com.spidex.timepad.data.RepeatInterval
import com.spidex.timepad.data.Task
import com.spidex.timepad.data.TaskInstance
import com.spidex.timepad.data.TaskRepository
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
fun DeleteDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
) {
    var deletePermanently by remember {
        mutableStateOf(false)
    }
    val currentTaskWithInstance by viewModel.currentTaskWithInstances.collectAsState()
    val deleteTaskWithInstance by viewModel.deleteTaskWithInstances.collectAsState()

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
                        text = "Delete",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font2)),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text =
                            if(deleteTaskWithInstance!!.first.repeatInterval!= RepeatInterval.NONE) {
                                if (deletePermanently) "Task will be Deleted Permanently"
                                else "Today Instance will be Deleted"
                            }
                            else{
                                "Task will be Deleted Permanently"
                            },
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.font2)),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TaskViewForDelete(task = deleteTaskWithInstance!!, deletePermanently){
                        deletePermanently = !deletePermanently
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if(deleteTaskWithInstance!!.first.repeatInterval == RepeatInterval.NONE){
                                    if(currentTaskWithInstance == deleteTaskWithInstance){
                                        viewModel.setCurrentTaskWithInstance(null)
                                    }
                                    viewModel.deleteTask(deleteTaskWithInstance!!)
                                    viewModel.onDeleteDone()
                                }
                                else
                                {
                                    if(currentTaskWithInstance == deleteTaskWithInstance){
                                        viewModel.setCurrentTaskWithInstance(null)
                                    }
                                    if(deletePermanently){
                                        viewModel.deleteTask(deleteTaskWithInstance!!)
                                        viewModel.onDeleteDone()
                                    }
                                    else
                                    {
                                        viewModel.deleteInstance(deleteTaskWithInstance!!.second)
                                        viewModel.onDeleteDone()
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
                                text = "Delete",
                                modifier = Modifier,
                                fontFamily = FontFamily(Font(R.font.font2)),
                                fontWeight = FontWeight.Bold
                            )
                        }


                        TextButton(onClick = {
                            onDismiss()
                        }) {
                            Text(
                                text = "Cancel",
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.font2)),
                            )
                        }
                    }
                }
            }
}

@Composable
fun TaskViewForDelete(task : Pair<Task, TaskInstance>, delete : Boolean, onClick : () -> Unit) {

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
                painter = painterResource(id = task.first.icon),
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
                text = task.first.title,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.font2)),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
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
                    if(task.first.repeatInterval!= RepeatInterval.NONE) {
                        when (delete) {
                            true -> red
                            false -> green
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
                            color = when (task.first.tag) {
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
                        text = task.first.tag,
                        color = when(task.first.tag){
                            "Work" -> red
                            "Coding" -> red
                            "Workout" -> orange
                            "Reading" -> green
                            "Project" -> purple
                            else -> silver
                        },
                        letterSpacing = 2.sp,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.font2)),
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
    val soundHelper = SoundHelper(LocalContext.current)
    val viewModel = TaskViewModel(taskRepository = TaskRepository(
        AppDatabase.getDatabase(
        LocalContext.current).taskDao()),
        soundHelper
    )
    DeleteDialog(viewModel){

    }
}