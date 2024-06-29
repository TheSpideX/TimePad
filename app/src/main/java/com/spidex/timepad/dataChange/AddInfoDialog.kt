package com.spidex.timepad.dataChange

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.core.text.isDigitsOnly
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.spidex.timepad.R
import com.spidex.timepad.data.RepeatInterval
import com.spidex.timepad.data.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year

@Composable
fun AddDialog(
    context: Context,
    onDismiss: () -> Unit,
    onTaskSaved : (Task) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        var selectedDay by remember {
            mutableStateOf(LocalDate.now())
        }

        var selectedTime by remember {
            mutableStateOf(LocalTime.now())
        }

        val focusManager = LocalFocusManager.current
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var durationMinutes by remember {
            mutableStateOf("")
        }
        val availableTags = listOf("Work", "Coding", "Workout", "Study", "Project", "Personal")
        var selectedTag by remember { mutableStateOf("Personal") }
        val availableRepeat = listOf("None", "Daily", "Weekly", "Monthly", "Yearly")
        var selectedRepeat by remember {
            mutableStateOf("None")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp)
                )
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text ="Add Task",
                fontFamily = FontFamily(Font(R.font.font2)),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
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
                text = "Schedule Date And Time",
                fontFamily = FontFamily(Font(R.font.font2)),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            WheelDateTimePicker(
                startDateTime = LocalDateTime.now().plusMinutes(5),
                minDateTime = LocalDateTime.now().plusMinutes(1),
                yearsRange = IntRange(Year.now().value,Year.now().value+100),
                size = DpSize(minOf(LocalConfiguration.current.screenWidthDp.dp,300.dp),100.dp),
                textColor = Color.Black,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    shape = RoundedCornerShape(20),
                    color = Color.White,
                    border = BorderStroke(1.dp,color = Color.Black)
                ),
                rowCount = 3
            ){snappedDateTime ->
                selectedTime = snappedDateTime.toLocalTime()
                selectedDay = snappedDateTime.toLocalDate()
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    if (title.isNotEmpty() && durationMinutes.isNotEmpty() && durationMinutes.isDigitsOnly()) {
                        val newTask = Task(
                            title = title,
                            description = description,
                            durationMinutes = durationMinutes.toLong(),
                            icon = when(selectedTag){
                                "Work" -> R.drawable.ic_work
                                "Coding" -> R.drawable.ic_code
                                "Workout" -> R.drawable.ic_workout
                                "Study" -> R.drawable.ic_study
                                "Project" -> R.drawable.ic_project
                                "Personal" -> R.drawable.ic_personal
                                else -> R.drawable.ic_personal
                            },
                            tag = selectedTag,
                            repeatInterval = when (selectedRepeat) {
                                "None" -> RepeatInterval.NONE
                                "Daily" -> RepeatInterval.DAILY
                                "Weekly" -> RepeatInterval.WEEKLY
                                "Monthly" -> RepeatInterval.MONTHLY
                                else -> RepeatInterval.YEARLY
                            },
                            createdAt = selectedDay,
                            scheduledTime = selectedTime
                        )
                        onTaskSaved(newTask)
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
                Text(
                    text = "Save",
                    fontFamily = FontFamily(Font(R.font.font2)),
                    )
            }

            TextButton(onClick = {
                onDismiss()
            },
                modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                Text(
                    text = "Cancel",
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.font2))
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddDialogPreview(){
    val context = LocalContext.current
    AddDialog(context = context, onDismiss = { /*TODO*/ }) {

    }
}