package com.example.basic_state_codelab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WellnessTask(val id: Int, val label: String,initialChecked: Boolean = false){
    var checked by mutableStateOf(initialChecked)
}
