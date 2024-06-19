@file:Suppress("DEPRECATION")

package com.spidex.timepad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoDialog(
    task : Task?,
    selectedDay: MutableState<LocalDate?>,
    onDismiss: () -> Unit,
    onTaskSaved : (Task) -> Unit,
    onTaskUpdate : (Task) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {

        var title by remember(task) { mutableStateOf(task?.title ?: "") }
        var description by remember(task) { mutableStateOf(task?.description ?:"") }
        var durationMinutes by remember(task) { mutableStateOf(task?.durationMinutes.toString() ?: "") }
        val availableTags = listOf("Work", "Personal", "Errands", "Meeting") // Sample tags
        var selectedTag by remember { mutableStateOf(task?.tag) }
        val availableRepeat = listOf("None","Daily","Weekly","Monthly","Yearly")
        var selectedRepeat by remember {
            mutableStateOf(
                when(task?.repeatInterval){
                    RepeatInterval.YEARLY -> "Yearly"
                    RepeatInterval.DAILY -> "Daily"
                    RepeatInterval.WEEKLY -> "Weekly"
                    RepeatInterval.MONTHLY -> "Monthly"
                    else -> "None"
                }
            ) }

        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(color = Color.White, shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Add Item",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false // Allow multiple lines for description
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = durationMinutes,
                onValueChange = { if (it.all { char -> char.isDigit() }) durationMinutes = it },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
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
                                fontSize = 12.sp
                            ) },
                        leadingIcon = if (tag == selectedTag) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected"
                                )
                            }
                        } else {
                            null
                        },
                        modifier = Modifier.padding(8.dp)
                            .wrapContentSize()
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
                                fontSize = 12.sp
                            ) },
                        leadingIcon = if (interval == selectedRepeat) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected"
                                )
                            }
                        } else {
                            null
                        },
                        modifier = Modifier.padding(8.dp)
                            .wrapContentSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val duration = durationMinutes.toLongOrNull() ?: 0
                val newTask = Task(
                    title = title,
                    description = description,
                    durationMinutes = duration,
                    tag = selectedTag,
                    repeatInterval = when(selectedRepeat){
                        "None" -> RepeatInterval.NONE
                        "Daily" -> RepeatInterval.DAILY
                        "Weekly" -> RepeatInterval.WEEKLY
                        "Monthly" -> RepeatInterval.MONTHLY
                        else -> RepeatInterval.YEARLY },
                    createdAt = selectedDay.value ?: LocalDate.now(),
                    scheduledDate = selectedDay.value ?: LocalDate.now()
                )
                if(task!=null){
                    onTaskUpdate(newTask)
                }
                else
                {
                    onTaskSaved(newTask)
                }

            }) {
                if(task!=null){
                    Text("Update")
                }
                else
                {
                    Text("Save")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoDialogPreview(){
    val date = remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val task = Task(
        title = "hello",
        description = "help",
        durationMinutes = 20,
        tag = "Work",
        repeatInterval = RepeatInterval.NONE
    )
    InfoDialog(task,date,{},{}){

    }
}