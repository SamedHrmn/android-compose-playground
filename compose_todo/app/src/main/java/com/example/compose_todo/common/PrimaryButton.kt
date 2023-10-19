package com.example.compose_todo.common
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose_todo.R
import com.example.compose_todo.ui.theme.TodoTheme

@Composable
fun TodoPrimaryButton(modifier: Modifier = Modifier,@StringRes textId:Int,onClick:() -> Unit) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), onClick = onClick) {
        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            style = MaterialTheme.typography.displayLarge.copy(color = Color.White),
            text = stringResource(
                id = textId
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodoPrimaryButtonPreview() {
    TodoTheme {
        TodoPrimaryButton(textId = R.string.save, onClick = {})
    }
}