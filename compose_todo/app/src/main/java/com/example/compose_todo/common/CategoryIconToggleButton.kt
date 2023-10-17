package com.example.compose_todo.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.compose_todo.R

enum class TodoCategory {
    NONE,
    WORK,
    GOAL,
    MEET;

    fun getIconRes(): Int? {
        return when (this) {
            WORK -> R.drawable.file_list_line_icon
            GOAL -> R.drawable.trophy_icon
            MEET -> R.drawable.calendar_event_icon
            else -> null
        }
    }

    fun getContainerColor(): Color? {
        return when (this) {
            WORK -> Color(0xFFDBECF6)
            GOAL -> Color(0xFFFEF5D3)
            MEET -> Color(0xFFE7E2F3)
            else -> null
        }
    }
}

@Composable
fun CategoryIconToggleButton(
    category: TodoCategory,
    isChecked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {


    if (category == TodoCategory.NONE) {
        Spacer(modifier = Modifier.size(0.dp))
    } else {


        Surface(shadowElevation = 1.dp, shape = CircleShape) {
            OutlinedIconToggleButton(
                checked = if (onCheckedChange == null) false else !isChecked,
                onCheckedChange = {checked->

                    onCheckedChange?.invoke(!checked)
                },
                shape = CircleShape,
                colors = IconButtonDefaults.iconToggleButtonColors(
                    containerColor = category.getContainerColor()!!
                ),
                border = BorderStroke(2.dp, color = Color.White),
                modifier = Modifier
                    .size(48.dp)
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(category.getIconRes()!!),
                    contentDescription = null
                )
            }
        }
    }


}