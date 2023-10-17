package com.example.compose_todo.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose_todo.ui.theme.TodoTheme

@Composable
fun TodoCheckBox(onChecked: (checked: Boolean) -> Unit, initialValue: Boolean = false) {
    var checked by remember {
        mutableStateOf(initialValue)
    }

    Surface(onClick = {
        checked = !checked
        onChecked(checked)
    }) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(1.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                .background(
                    color = if (checked) MaterialTheme.colorScheme.primary else Color.White,
                    shape = RoundedCornerShape(3.dp),
                )


        ) {
            if (checked) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoCheckBoxPreview() {
    TodoTheme {
        Column(
            modifier = Modifier.size(100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { TodoCheckBox(onChecked = {}, initialValue = true) }
    }
}