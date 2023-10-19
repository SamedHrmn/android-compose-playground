package com.example.compose_todo.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.example.compose_todo.util.ProjectUtils

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoDatePicker(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedDate: String
) {

    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = ProjectUtils.convertDateToMillis(initialSelectedDate))
    val selectedDate = datePickerState.selectedDateMillis?.let {
        ProjectUtils.convertMillisToDate(it)
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