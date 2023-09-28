package com.example.basic_state_codelab

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier,
    wellnessViewModel: WellnessViewModel = viewModel()
) {
    Column {
        WaterCounter(modifier)
        WellnessTasksList(list = wellnessViewModel.tasks, onCheckedTask = { task, checked ->
            wellnessViewModel.changeTaskChecked(task, checked)
        }, onCloseTask = { task -> wellnessViewModel.remove(task) })
    }
}
