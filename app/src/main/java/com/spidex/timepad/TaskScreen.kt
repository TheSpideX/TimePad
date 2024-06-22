package com.spidex.timepad

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.exyte.animatednavbar.utils.noRippleClickable
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun TaskScreen(viewModel: TaskViewModel,context : Context,navigateToClock: () -> Unit) {
    val currentMonth = remember { viewModel.currentMonth.value }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { WeekFields.of(Locale.getDefault()).firstDayOfWeek }
    val showDialog by viewModel.showDialog.collectAsState()
    val deleteDialog by viewModel.showDeleteDialog.collectAsState()

    val state = rememberCalendarState(
        startMonth = currentMonth,
        endMonth = endMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )

    val currentMonthDisplayed = state.firstVisibleMonth.yearMonth
    val selectedDay = remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp)
                .background(color = background)
        ) {

            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    MonthHeader(currentMonthDisplayed)

                    WeekdayHeader()
                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalCalendar(
                        state = state,
                        modifier = Modifier,
                        dayContent = { day ->
                            if (day.position == DayPosition.MonthDate) {
                                Day(
                                    viewModel = viewModel,
                                    def = 1,
                                    day = day,
                                    isSelected = selectedDay.value == day.date,
                                    onClick = {
                                        selectedDay.value = day.date
                                        viewModel.getTasksForDate(day.date)
                                    }
                                )
                            } else {
                                Day(
                                    viewModel = viewModel,
                                    def = 0,
                                    day = day,
                                    isSelected = selectedDay.value == day.date,
                                    onClick = {
                                        selectedDay.value = day.date
                                        viewModel.getTasksForDate(day.date)
                                    }
                                )
                            }
                        },

                        )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tasks for Selected Date",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            TaskListForDate(viewModel, selectedDay.value ?: LocalDate.now(), context,navigateToClock)
        }

        FloatingActionButton(
            onClick = {
                      viewModel.setShowDialog(true)
            },
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .size(48.dp),
            containerColor = Color.White,
            shape = RoundedCornerShape(20)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
        }


        if(showDialog){
            InfoDialog(
                viewModel = viewModel ,
                selectedDay = selectedDay,
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
                viewModel.getTasksForDate(selectedDay.value ?: LocalDate.now())
            }
        }
    }
}

@Composable
fun WeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        (0..6).forEach { index ->
            Text(
                text = DayOfWeek.of((index + 1) % 7 + 1)
                    .getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MonthHeader(currentMonth: YearMonth) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() } + " " + currentMonth.year,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Day(viewModel: TaskViewModel,def : Int,day: CalendarDay, isSelected: Boolean, onClick: () -> Unit) {

    val color : Boolean = viewModel.checkForTask(day.date)

    Box(
        modifier = Modifier
            .size(42.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(20))
            .background(
                color = if (isSelected) Color.Black
                else Color.White
            )
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = when(isSelected){
                true -> Color.White
                false -> when(def){
                    1 -> Color.Black
                    else -> Color.Gray
                }
            }
        )

        if(color){
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(4.dp)
                    .offset(x = (-4).dp, y = (4).dp)
                    .clip(CircleShape),
                containerColor = if(isSelected) Color.White else Color.Red
            ) {
            }
        }
    }
}

@Composable
fun TaskListForDate(viewModel: TaskViewModel, date: LocalDate,context: Context,navigateToClock: () -> Unit) {
    viewModel.getTasksForDate(date)
    val tasks by viewModel.tasksForDate.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(tasks) {
            TaskViewTaskScreen(viewModel,it,context,navigateToClock)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskViewTaskScreen(viewModel: TaskViewModel, task : Task, context : Context, navigateToClock: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(20)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .combinedClickable(
                    onClick = {
                        if (task.status != TaskStatus.COMPLETED) {
                            viewModel.setCurrentTask(task)
                            viewModel.startOrResumeTimer()
                            navigateToClock()
                        } else {
                            Toast
                                .makeText(context, "Task is Already Completed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    onDoubleClick = {
                        viewModel.setEditTask(task)
                        viewModel.setShowDialog(true)
                    },
                    onLongClick = {
                        viewModel.setDeleteTask(task)
                        viewModel.setShowDeleteDialog(true)
                    },
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

            Card(
                modifier = Modifier
                    .width(32.dp)
                    .height(8.dp)
                    .constrainAs(time) {
                        top.linkTo(parent.top, margin = 16.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    },
                colors = CardDefaults.cardColors(containerColor = when(task.status){
                    TaskStatus.NOT_STARTED -> red
                    TaskStatus.COMPLETED -> green
                    else -> Color(0xfFFFA656)
                })
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
                    .noRippleClickable {
                        viewModel.setCurrentTask(task)
                        viewModel.startOrResumeTimer()
                        navigateToClock()
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
fun TaskScreenPreview() {
    val context = LocalContext.current
//    val task = Task(
//        id = 0,
//        title = "Project",
//        durationMinutes = 1,
//        tag = "Coding",
//        icon = R.drawable.ic_code,
//    )
    val taskViewModel = TaskViewModel(taskRepository = TaskRepository(AppDatabase.getDatabase(LocalContext.current).taskDao()))
    TaskScreen(taskViewModel,context){

    }

//    TaskView(viewModel = taskViewModel, task =task ) {
//
//    }
}