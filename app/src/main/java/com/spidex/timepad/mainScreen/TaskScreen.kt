package com.spidex.timepad.mainScreen

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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.exyte.animatednavbar.utils.noRippleClickable
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.spidex.timepad.R
import com.spidex.timepad.SoundHelper
import com.spidex.timepad.viewModel.TaskViewModel
import com.spidex.timepad.data.AppDatabase
import com.spidex.timepad.data.Task
import com.spidex.timepad.data.TaskInstance
import com.spidex.timepad.data.TaskInstanceStatus
import com.spidex.timepad.data.TaskRepository
import com.spidex.timepad.dataChange.AddDialog
import com.spidex.timepad.dataChange.DeleteDialog
import com.spidex.timepad.dataChange.EditDialog
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
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun TaskScreen(viewModel: TaskViewModel, context : Context, navigateToAllTask :() -> Unit, navigateToClock: () -> Unit) {
    val firstDayOfWeek = remember { WeekFields.of(Locale.getDefault()).firstDayOfWeek }
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val showEditDialog by viewModel.showEditDialog.collectAsState()
    val deleteDialog by viewModel.showDeleteDialog.collectAsState()
    val state = rememberCalendarState(
        startMonth = YearMonth.now().minusMonths(100),
        endMonth = YearMonth.now().plusMonths(100),
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow,
        firstVisibleMonth = YearMonth.now()
    )

    val currentMonthDisplayed = state.firstVisibleMonth.yearMonth
    val selectedDay by viewModel.selectedDate.collectAsState()

    LaunchedEffect(currentMonthDisplayed) {
        viewModel.setCurrentMonth(currentMonthDisplayed)
    }

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
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MonthHeader(viewModel,currentMonthDisplayed, state)

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
                                    isSelected = selectedDay == day.date,
                                    onClick = {date->
                                       viewModel.setDay(date)
                                    }
                                )
                            } else {
                                Day(
                                    viewModel = viewModel,
                                    def = 0,
                                    day = day,
                                    isSelected = selectedDay == day.date,
                                    onClick = {date->
                                        viewModel.setDay(date)
                                    }
                                )
                            }
                        },

                        )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Tasks for Selected Date",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.font2))
                )
                FloatingActionButton(
                    onClick = {
                        navigateToAllTask()
                    },
                    modifier = Modifier.size(32.dp),
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        hoveredElevation = 4.dp,
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_setting),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            TaskListForDate(viewModel, context, navigateToClock)

        }


        FloatingActionButton(
            onClick = {
                viewModel.setAddDialog(true)
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

        if(showAddDialog)
        {
            AddDialog(context = context, onDismiss = {
                viewModel.setAddDialog(false)
            }) {task->
                viewModel.addNewTask(task)
                viewModel.setAddDialog(false)
            }
        }

        if(showEditDialog){
            EditDialog(viewModel = viewModel, context = context) {
                viewModel.setEditDialog(false)
                viewModel.doneEditing()
            }
        }

        if(deleteDialog){
            DeleteDialog(viewModel = viewModel) {
                viewModel.setShowDeleteDialog(false)
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
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                fontFamily = FontFamily(Font(R.font.font2))
            )
        }
    }
}

@Composable
fun MonthHeader(viewModel: TaskViewModel, currentMonth: YearMonth, calendarState: CalendarState) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = 8.dp, end = 8.dp)
            .padding(bottom = 16.dp)
            .clip(shape = RoundedCornerShape(20)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() } + " " + currentMonth.year,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily(Font(R.font.font2)),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                .clip(shape = RoundedCornerShape(20))
                .noRippleClickable {
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(YearMonth.now())
                        viewModel.setDay(LocalDate.now())
                    }
                }
        )
    }
}

@Composable
fun Day(viewModel: TaskViewModel, def : Int, day: CalendarDay, isSelected: Boolean, onClick: (LocalDate) -> Unit) {

    val color : Boolean = viewModel.checkForTaskOnDate(day.date)

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
                onClick = { onClick(day.date) }
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
            },
            fontFamily = FontFamily(Font(R.font.font2))
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
fun TaskListForDate(viewModel: TaskViewModel, context: Context, navigateToClock: () -> Unit) {
    val tasks by viewModel.tasksForDateWithInstances.collectAsState()
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
fun TaskViewTaskScreen(viewModel: TaskViewModel, task : Pair<Task, TaskInstance>, context : Context, navigateToClock: () -> Unit) {

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
                        if (task.second.status != TaskInstanceStatus.COMPLETED) {
                            viewModel.setCurrentTaskWithInstance(task)
                            viewModel.startOrResumeTimer()
                            navigateToClock()
                        } else {
                            Toast
                                .makeText(context, "Task is Already Completed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    onDoubleClick = {
                        if (task.second.status != TaskInstanceStatus.COMPLETED) {
                            viewModel.setEditTask(task)
                            viewModel.setEditDialog(true)
                        } else {
                            Toast
                                .makeText(context, "Task is Already Completed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    onLongClick = {
                        viewModel.setDeleteTask(task)
                        viewModel.setShowDeleteDialog(true)
                    },
                )
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
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.font2)),
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
                colors = CardDefaults.cardColors(containerColor = when(task.second.status){
                    TaskInstanceStatus.NOT_STARTED -> red
                    TaskInstanceStatus.COMPLETED -> green
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
                        if (task.second.status != TaskInstanceStatus.COMPLETED) {
                            viewModel.setCurrentTaskWithInstance(task)
                            viewModel.startOrResumeTimer()
                            navigateToClock()
                        } else {
                            Toast
                                .makeText(context, "Task is Already Completed", Toast.LENGTH_SHORT)
                                .show()
                        }
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
                                "Study" -> lightGreen
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
                            "Study" -> green
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
fun TaskScreenPreview() {
    val context = LocalContext.current

    val soundHelper = SoundHelper(LocalContext.current)
    val viewModel = TaskViewModel(taskRepository = TaskRepository(
        AppDatabase.getDatabase(
        LocalContext.current).taskDao()),
        soundHelper
    )
    TaskScreen(viewModel,context,{}){

    }

}