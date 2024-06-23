package com.spidex.timepad

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoDialog(
    viewModel: TaskViewModel,
    selectedDay: MutableState<LocalDate?>?,
    context: Context,
    onDismiss: () -> Unit,
    onTaskSaved : (Task) -> Unit,
    onTaskUpdate : (Task) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {

        val task by viewModel.editTask.collectAsState()
        val focusManager = LocalFocusManager.current
        var title by remember(task) { mutableStateOf(task?.title ?: "") }
        var description by remember(task) { mutableStateOf(task?.description ?: "") }
        var durationMinutes by remember(task) {
            mutableStateOf(
                task?.durationMinutes?.toString() ?: ""
            )
        }
        val availableTags = listOf("Work", "Coding", "Workout", "Study", "Project", "Personal")
        var selectedTag by remember { mutableStateOf(task?.tag ?: "Personal") }
        val availableRepeat = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
        var selectedRepeat by remember {
            mutableStateOf(
                when (task?.repeatInterval) {
                    RepeatInterval.YEARLY -> "Yearly"
                    RepeatInterval.DAILY -> "Daily"
                    RepeatInterval.WEEKLY -> "Weekly"
                    RepeatInterval.MONTHLY -> "Monthly"
                    else -> "None"
                }
            )
        }

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = if(task==null) "Add Task" else "Update Task",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = if(title.isNotEmpty()) Color.Black else Color.Gray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = if(title.isNotEmpty()) Color.Black else Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = if(title.isNotEmpty()) Color.Black else Color.Gray
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
                        unfocusedBorderColor = if(description.isNotEmpty()) Color.Black else Color.Gray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = if(description.isNotEmpty()) Color.Black else Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = if(description.isNotEmpty()) Color.Black else Color.Gray
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
                    onValueChange = { if (it.all { char -> char.isDigit() }) durationMinutes = it },
                    label = { Text("Duration (minutes)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = if(durationMinutes.isNotEmpty()) Color.Black else Color.Gray,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = if(durationMinutes.isNotEmpty()) Color.Black else Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = if(durationMinutes.isNotEmpty()) Color.Black else Color.Gray
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Tag:",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    availableTags.forEach { tag ->
                        FilterChip(
                            selected = tag == selectedTag,
                            onClick = { selectedTag = tag },
                            label = {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    color = if (selectedTag == tag) Color.White else Color.Gray
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
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
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
                                    color = if (selectedRepeat == interval) Color.White else Color.Gray
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
                            if(task!=null)
                            {
                                val oldDuration = task!!.durationMinutes
                                val oldRemTime = task!!.remainingTimeMillis
                                val newTask = task!!.copy(
                                    title = title,
                                    icon = when(selectedTag){
                                        "Work" -> R.drawable.ic_work
                                        "Coding" -> R.drawable.ic_code
                                        "Workout" -> R.drawable.ic_workout
                                        "Study" -> R.drawable.ic_study
                                        "Project" -> R.drawable.ic_project
                                        "Personal" -> R.drawable.ic_personal
                                        else -> R.drawable.ic_personal
                                    },
                                    description = description,
                                    durationMinutes = durationMinutes.toLong(),
                                    remainingTimeMillis =if(durationMinutes.toLong() == oldDuration)
                                        oldRemTime
                                    else
                                        durationMinutes.toLong() * 60 * 1000L,
                                    tag = selectedTag,
                                    repeatInterval = when (selectedRepeat) {
                                        "None" -> RepeatInterval.NONE
                                        "Daily" -> RepeatInterval.DAILY
                                        "Weekly" -> RepeatInterval.WEEKLY
                                        "Monthly" -> RepeatInterval.MONTHLY
                                        else -> RepeatInterval.YEARLY
                                    }
                                )

                                onTaskUpdate(newTask)
                            }
                            else {
                                val newTask = Task(
                                    title = title,
                                    icon = when (selectedTag) {
                                        "Work" -> R.drawable.ic_work
                                        "Coding" -> R.drawable.ic_code
                                        "Workout" -> R.drawable.ic_workout
                                        "Study" -> R.drawable.ic_study
                                        "Project" -> R.drawable.ic_project
                                        "Personal" -> R.drawable.ic_personal
                                        else -> R.drawable.ic_personal
                                    },
                                    description = description.ifEmpty { "Description" },
                                    durationMinutes = durationMinutes.toLong(),
                                    tag = selectedTag,
                                    repeatInterval = when (selectedRepeat) {
                                        "None" -> RepeatInterval.NONE
                                        "Daily" -> RepeatInterval.DAILY
                                        "Weekly" -> RepeatInterval.WEEKLY
                                        "Monthly" -> RepeatInterval.MONTHLY
                                        else -> RepeatInterval.YEARLY
                                    },
                                    createdAt = selectedDay?.value ?: LocalDate.now(),
                                    scheduledDate = selectedDay?.value ?: LocalDate.now()
                                )
                                onTaskSaved(newTask)
                            }
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
                            Toast.makeText(context, "Duration Cannot Be Empty", Toast.LENGTH_LONG)
                                .show()
                        } else if (!durationMinutes.isDigitsOnly()) {
                            Toast.makeText(
                                context,
                                "Duration Can only Be Digits",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "Something Went Wrong...", Toast.LENGTH_LONG)
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
                    if (task != null) {
                        Text("Update")
                    } else {
                        Text("Save")
                    }
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

@Preview(showBackground = true)
@Composable
fun InfoDialogPreview(){
    val date = remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val context = LocalContext.current
    val taskViewModel = TaskViewModel(taskRepository = TaskRepository(AppDatabase.getDatabase(LocalContext.current).taskDao()))
    InfoDialog(taskViewModel,date,context,{},{}){

    }
}