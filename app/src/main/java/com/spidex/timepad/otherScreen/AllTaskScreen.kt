package com.spidex.timepad.otherScreen

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
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
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AllTaskScreen(navController: NavController, viewModel: TaskViewModel, context : Context){

    BackHandler {
        navController.navigateUp()
    }

    val allTask by viewModel.allTasks.collectAsState()
    val showEditDialog by viewModel.showEditDialog.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val (detail,back) = createRefs()

            IconButton(
                onClick = {
                    navController.navigateUp()
                },
                modifier = Modifier
                    .constrainAs(back) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .offset(x = (-2).dp, y = (-2).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp)
                    .constrainAs(detail) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "All Task",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.font2)),
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(allTask){task->
                        TaskViewForAllTaskScreen(viewModel,task)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if(showEditDialog){
                    EditDialogForAllScreen(
                        viewModel = viewModel,
                        context = context,
                        onDismiss = {
                            viewModel.setEditDialog(false)
                            viewModel.setEditTask(null)
                        }
                    )
                }

                if(showDeleteDialog){
                    DeleteDialogForAllTask(viewModel){
                        viewModel.setShowDeleteDialog(false)
                        viewModel.setDeleteTask(null)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskViewForAllTaskScreen(viewModel: TaskViewModel, task : Task) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(when(task.isDeleted){
            true -> 0.dp
            false -> 4.dp
        }),
        shape = RoundedCornerShape(20),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .combinedClickable(
                    onClick = {

                    },
                    onDoubleClick = {
                        if (!task.isDeleted) {
                            val instance = task.toTaskInstance(LocalDate.now())
                            viewModel.setEditTask(Pair(task, instance))
                            viewModel.setEditDialog(true)
                        }
                    },
                    onLongClick = {

                        val instance = task.toTaskInstance(LocalDate.now())
                        viewModel.setDeleteTask(Pair(task, instance))
                        viewModel.setShowDeleteDialog(true)
                    }
                )
        ) {
            val (image, title, tag, time, play) = createRefs()
            Image(
                painter = painterResource(id = task.icon),
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
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.font2)),
                modifier = Modifier
                    .width(130.dp)
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
                shape = RoundedCornerShape(20),
                colors = CardDefaults.cardColors(containerColor = when(task.isDeleted){
                    true -> red
                    else -> green
                })
            ) {}


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
                                "Study" -> lightGreen
                                "Project" -> lightPurple
                                else -> lightSilver
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                )
                {
                    Text(
                        text = task.tag,
                        color = when(task.tag){
                            "Work" -> red
                            "Coding" -> red
                            "Workout" -> orange
                            "Study" -> green
                            "Project" -> purple
                            else -> silver
                        },
                        letterSpacing = 2.sp,
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.font2)),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 8.dp,end = 8.dp,top = 4.dp,bottom = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun EditDialogForAllScreen(
    viewModel: TaskViewModel,
    context: Context,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        val taskWithInstance by viewModel.editTaskWithInstances.collectAsState()
        var selectedTime by remember {
            mutableStateOf(taskWithInstance!!.first.scheduledTime)
        }
        val focusManager = LocalFocusManager.current
        var title by remember(taskWithInstance?.first) { mutableStateOf(taskWithInstance?.first?.title ?: "") }
        var description by remember(taskWithInstance?.first) { mutableStateOf(taskWithInstance?.first?.description ?: "") }
        var durationMinutes by remember(taskWithInstance?.first) {
            mutableStateOf(
                taskWithInstance?.first?.durationMinutes?.toString() ?: ""
            )
        }
        val availableTags = listOf("Work", "Coding", "Workout", "Study", "Project", "Personal")
        var selectedTag by remember { mutableStateOf(taskWithInstance?.first?.tag ?: "Personal") }
        val availableRepeat = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
        var selectedRepeat by remember {
            mutableStateOf(
                when (taskWithInstance?.first?.repeatInterval) {
                    RepeatInterval.YEARLY -> "Yearly"
                    RepeatInterval.DAILY -> "Daily"
                    RepeatInterval.WEEKLY -> "Weekly"
                    RepeatInterval.MONTHLY -> "Monthly"
                    else -> "None"
                }
            )
        }

        val configuration = LocalConfiguration.current
        val screenHeight = (configuration.screenHeightDp * 0.9).dp

        var dialogContentHeight by remember { mutableStateOf(0.dp) }
        var useMaxHeight by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    dialogContentHeight = coordinates.size.height.dp
                    useMaxHeight = dialogContentHeight > screenHeight
                }
                .let {
                    if (useMaxHeight) {
                        it
                            .heightIn(max = screenHeight)
                            .verticalScroll(rememberScrollState())
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp)
                            )
                    } else {
                        it
                            .wrapContentHeight()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp)
                            )
                    }
                }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text ="Update Task",
                fontFamily = FontFamily(Font(R.font.font2)),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "All Future Instance will be Updated",
                fontFamily = FontFamily(Font(R.font.font2)),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            TaskViewForEditForAllScreen(task = taskWithInstance, edit = true)

            Spacer(modifier = Modifier.height(8.dp))

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = if (title.isNotEmpty()) Color.Black else Color.Gray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = if (title.isNotEmpty()) Color.Black else Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = if (title.isNotEmpty()) Color.Black else Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))



                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = if (description.isNotEmpty()) Color.Black else Color.Gray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = if (description.isNotEmpty()) Color.Black else Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = if (description.isNotEmpty()) Color.Black else Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = durationMinutes,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) durationMinutes = it
                },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = if (durationMinutes.isNotEmpty()) Color.Black else Color.Gray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = if (durationMinutes.isNotEmpty()) Color.Black else Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = if (durationMinutes.isNotEmpty()) Color.Black else Color.Gray
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reminder Time",
                fontFamily = FontFamily(Font(R.font.font2)),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            WheelTimePicker(
                startTime = taskWithInstance?.first?.scheduledTime ?: LocalTime.now(),
                size = DpSize(minOf(LocalConfiguration.current.screenWidthDp.dp,300.dp),100.dp),
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    shape = RoundedCornerShape(20),
                    color = Color.White,
                    border = BorderStroke(1.dp,color = Color.Black)
                ),
                onSnappedTime = {time->
                    selectedTime = time
                }
            )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select Tag",
                    fontFamily = FontFamily(Font(R.font.font2)),
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                ) {
                    availableTags.forEach { tag ->
                        FilterChip(
                            selected = tag == selectedTag,
                            onClick = { selectedTag = tag },
                            label = {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    color = if (selectedTag == tag) Color.White else Color.Gray,
                                    fontFamily = FontFamily(Font(R.font.font2)),
                                )
                            },
                            leadingIcon = if (tag == selectedTag) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .wrapContentSize(),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Repeat Interval ",
                    fontFamily = FontFamily(Font(R.font.font2)),
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                ) {
                    availableRepeat.forEach { interval ->
                        FilterChip(
                            selected = interval == selectedRepeat,
                            onClick = {
                                selectedRepeat = interval

                            },
                            label = {
                                Text(
                                    text = interval,
                                    fontSize = 12.sp,
                                    color = if (selectedRepeat == interval) Color.White else Color.Gray,
                                    fontFamily = FontFamily(Font(R.font.font2)),
                                )
                            },
                            leadingIcon = if (interval == selectedRepeat) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White
                                    )
                                }
                            } else {
                                null
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .wrapContentSize(),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black
                            )
                        )
                    }
                }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                        if (title.isNotEmpty() && durationMinutes.isDigitsOnly() && durationMinutes.isNotEmpty()) {
                            val updatedTask = taskWithInstance!!.first.copy(
                                title = title,
                                description = description,
                                durationMinutes = durationMinutes.toLong(),
                                repeatInterval = when (selectedRepeat) {
                                    "Yearly" -> RepeatInterval.YEARLY
                                    "Monthly" -> RepeatInterval.MONTHLY
                                    "Weekly" -> RepeatInterval.WEEKLY
                                    "Daily" -> RepeatInterval.DAILY
                                    else -> RepeatInterval.NONE
                                },
                                scheduledTime = selectedTime,
                                tag = selectedTag,
                                icon = when(selectedTag){
                                    "Work" -> R.drawable.ic_work
                                    "Coding" -> R.drawable.ic_code
                                    "Workout" -> R.drawable.ic_workout
                                    "Study" -> R.drawable.ic_study
                                    "Project" -> R.drawable.ic_project
                                    "Personal" -> R.drawable.ic_personal
                                    else -> R.drawable.ic_personal
                                }
                            )

                            viewModel.updateTask(updatedTask, true,taskWithInstance!!.second)
                            viewModel.setEditDialog(false)
                            onDismiss()

                        } else if (title.isEmpty() && durationMinutes.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Title and Duration Cannot Be Empty",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (title.isEmpty()) {
                            Toast.makeText(context, "Title Cannot Be Empty", Toast.LENGTH_LONG)
                                .show()
                        } else if (durationMinutes.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Duration Cannot Be Empty",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } else if (!durationMinutes.isDigitsOnly()) {
                            Toast.makeText(
                                context,
                                "Duration Can only Be Digits",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Something Went Wrong...",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20)
            ) {
                Text(
                    text = "Update",
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
                    fontFamily = FontFamily(Font(R.font.font2))
                )
            }
        }
    }
}

@Composable
fun DeleteDialogForAllTask(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
) {
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
                    text =  "Task will be Deleted Permanently",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.font2)),
                )

                Spacer(modifier = Modifier.height(24.dp))

                TaskViewForEditForAllScreen(task = deleteTaskWithInstance!!, true)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if(deleteTaskWithInstance!!.first.repeatInterval == RepeatInterval.NONE){
                            if(currentTaskWithInstance == deleteTaskWithInstance){
                                viewModel.setCurrentTaskWithInstance(null)
                            }
                            viewModel.deleteTaskPermanently(deleteTaskWithInstance!!)
                            viewModel.onDeleteDone()
                        }
                        else
                        {
                            if(currentTaskWithInstance == deleteTaskWithInstance){
                                viewModel.setCurrentTaskWithInstance(null)
                            }
                                viewModel.deleteTaskPermanently(deleteTaskWithInstance!!)
                            viewModel.onDeleteDone()
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
fun TaskViewForEditForAllScreen( task : Pair<Task, TaskInstance>?, edit : Boolean) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            val (image, title, tag, time, play) = createRefs()
            Image(
                painter = painterResource(id = task!!.first.icon),
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
                colors = CardDefaults.cardColors(
                    containerColor =
                    if (task.first.repeatInterval != RepeatInterval.NONE) {
                        when (edit) {
                            true -> red
                            false -> green
                        }
                    } else {
                        red
                    }
                )
            ) {}

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
                        color = when (task.first.tag) {
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
                        modifier = Modifier.padding(
                            start = 8.dp,
                            end = 8.dp,
                            top = 2.dp,
                            bottom = 2.dp
                        )
                    )
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun AllTaskScreenPreview() {
    val soundHelper = SoundHelper(LocalContext.current)
    val viewModel = TaskViewModel(taskRepository = TaskRepository(
        AppDatabase.getDatabase(
        LocalContext.current).taskDao()),
        soundHelper
    )
    val context = LocalContext.current
    val navController = rememberNavController()
    AllTaskScreen(navController = navController, viewModel = viewModel, context = context)
}