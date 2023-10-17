package com.example.compose_todo.common

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("SupportAnnotationUsage")
@Composable
fun TextFieldWithTitle(
    modifier: Modifier = Modifier,
    @StringRes title: String,
    @StringRes hintText: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLines: Int = 1,
    value: String = "",
    minLines:Int = 1,
    readOnly: Boolean = false,

    onValueChange: ((String) -> Unit)? = null
) {
    var inputText by remember {
        mutableStateOf("")
    }

    Column {
        val textFieldModifier = modifier
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(6.dp)
            )

        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 14.sp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = if (readOnly) value else inputText,
            readOnly = readOnly,
            minLines = minLines,
            maxLines = maxLines,
            onValueChange = { v ->
                inputText = v
                onValueChange?.let { it(inputText) }
            },
            placeholder = {
                Text(
                    text = hintText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.typography.bodyMedium.color.copy(
                            alpha = 0.7F
                        )
                    )
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,

                ),
            trailingIcon = trailingIcon,
            modifier = if (readOnly) textFieldModifier.noRippleClickable { } else textFieldModifier

        )

    }


}

inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier =
    composed {
        clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
            onClick()
        }
    }