package com.example.compose_todo.view

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.compose_todo.R
import com.example.compose_todo.common.CategoryIconToggleButton
import com.example.compose_todo.common.TextFieldWithTitle
import com.example.compose_todo.common.TodoCategory
import com.example.compose_todo.common.TodoPrimaryButton
import com.example.compose_todo.data.Todos
import com.example.compose_todo.ui.theme.TodoTheme
import com.example.compose_todo.util.ProjectUtils.convertDateToMillis
import com.example.compose_todo.util.ProjectUtils.convertMillisToDate
import com.example.compose_todo.viewmodel.TodoViewModel
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTodoScreen(navController: NavController,todoViewModel: TodoViewModel? = null) {
    var selectedDate by remember {
        mutableStateOf("")
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    var note by remember {
        mutableStateOf("")
    }

    var title by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableIntStateOf(0)
    }

    var isDatePickerVisible by remember { mutableStateOf(false) }
    var isTimePickerVisible by remember { mutableStateOf(false) }


    Surface(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
        Column {
            TextFieldWithTitle(
                title = stringResource(id = R.string.task_title),
                hintText = stringResource(
                    id = R.string.task_title
                ),
                onValueChange = {
                    title = it
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.width(24.dp))
                CategoryIconToggleButton(
                    category = TodoCategory.WORK,
                    isChecked = selectedCategory == TodoCategory.values()
                        .indexOf(TodoCategory.WORK),
                    onCheckedChange = {
                        if (it) selectedCategory = TodoCategory.values().indexOf(TodoCategory.WORK)
                    },
                )
                Spacer(modifier = Modifier.width(16.dp))
                CategoryIconToggleButton(
                    category = TodoCategory.MEET,
                    isChecked = selectedCategory == TodoCategory.values()
                        .indexOf(TodoCategory.MEET),
                    onCheckedChange = {
                        if (it) selectedCategory = TodoCategory.values().indexOf(TodoCategory.MEET)
                    },
                )
                Spacer(modifier = Modifier.width(16.dp))
                CategoryIconToggleButton(
                    category = TodoCategory.GOAL,
                    isChecked = selectedCategory == TodoCategory.values()
                        .indexOf(TodoCategory.GOAL),
                    onCheckedChange = {
                        if (it) selectedCategory = TodoCategory.values().indexOf(TodoCategory.GOAL)
                    },
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                Surface(
                    modifier = Modifier

                        .weight(1f)
                ) {
                    TextFieldWithTitle(
                        title = "Date",
                        hintText = "Date",
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { isDatePickerVisible = true }) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = null
                                )
                            }
                        },
                        value = selectedDate
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    TextFieldWithTitle(
                        title = "Time",
                        hintText = "Time",

                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { isTimePickerVisible = true }) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null
                                )
                            }
                        },
                        value = selectedTime
                    )
                }
            }

            TextFieldWithTitle(
                modifier = Modifier.fillMaxWidth(),
                title = "Notes",
                hintText = "Notes",
                minLines = 6,
                maxLines = 6,
                onValueChange = { note = it },
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface {
                TodoPrimaryButton(textId = R.string.save, onClick = {
                    todoViewModel!!.addTodo(
                        Todos(
                            id = null,
                            title = title,
                            notes = note,
                            date = selectedDate,
                            time = selectedTime,
                            category = selectedCategory,
                            isCompleted = false,
                        )
                    )
                    navController.popBackStack()
                })
            }

            if (isDatePickerVisible) {
                TodoDatePicker(
                    initialSelectedDate = selectedDate,
                    onDateSelected = {
                        selectedDate = it
                        isDatePickerVisible = false
                    }, onDismiss = {
                        isDatePickerVisible = false
                    })
            }
            if (isTimePickerVisible) {
                TodoTimePicker(onCancel = {
                    isTimePickerVisible = false
                }, onConfirm = {
                    selectedTime = it
                    isTimePickerVisible = false
                }, initialTime = selectedTime)
            }

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoDatePicker(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedDate: String
) {

    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = convertDateToMillis(initialSelectedDate))
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        Button(onClick = {
            onDateSelected(selectedDate)
        }) {
            Text(text = "OK")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTimePicker(
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit, initialTime: String
) {
    val initialHour =
        if (initialTime.contains(":")) initialTime.split(":")[0].toInt() else LocalDateTime.now().hour
    val initialMinute =
        if (initialTime.contains(":")) initialTime.split(":")[1].toInt() else LocalDateTime.now().minute
    val timePickerState =
        rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute)

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Select Time",
                    style = MaterialTheme.typography.labelMedium
                )
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {

                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = {
                            onConfirm(timePickerState.hour.toString() + ":" + timePickerState.minute.toString())
                        }
                    ) { Text("OK") }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    showBackground = true, showSystemUi = true,
    device = "id:pixel_3",
    uiMode = UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun AddTodoScreenPreview() {
    TodoTheme {
        AddTodoScreen(rememberNavController())
    }
}