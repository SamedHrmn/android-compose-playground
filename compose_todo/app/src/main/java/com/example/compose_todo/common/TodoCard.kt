package com.example.compose_todo.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose_todo.data.Todos
import com.example.compose_todo.ui.theme.TodoTheme

@Composable
fun TodoCard(
    todo: Todos,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onChecked: (checked: Boolean) -> Unit
) {
    var isExpand by remember {
        mutableStateOf(false)
    }

    var cardShape = RoundedCornerShape(0.dp)
    if (isFirst) {
        cardShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    } else if (isLast) {
        cardShape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
    }

    Surface(
        onClick = {
            isExpand = !isExpand
        },
        modifier = Modifier
            .padding(top = if (isFirst) 12.dp else 0.dp)
            .shadow(4.dp, shape = cardShape, clip = true)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            CategoryIconToggleButton(category = TodoCategory.values()[todo.category])
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(
                        spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600),
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                )
                Text(
                    text = todo.time,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFF1B1B1D).copy(alpha = 0.7F)
                    )
                )
                if (isExpand) {
                    Text(text = todo.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
            TodoCheckBox(onChecked = onChecked, initialValue = todo.isCompleted)
        }

    }
}


@Preview(showBackground = true)
@Composable
fun TodoCardPreview() {
    TodoTheme {
        Surface(modifier = Modifier.fillMaxHeight(), color = Color(0xFFF1F5F9)) {
            Column(modifier = Modifier.padding(16.dp)) {
                TodoCard(
                    todo = Todos(
                        title = "Test",
                        id = null,
                        category = 1,
                        date = "20.10.2023",
                        time = "19:00",
                        isCompleted = false,
                        notes = "Test"
                    ),
                    isFirst = true,
                    onChecked = {}
                )
            }
        }
    }
}