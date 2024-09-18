package com.example.seminoma_analyzer.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.seminoma_analyzer.ml.ModelType
import com.example.seminoma_analyzer.ui.theme.Typography
import kotlinx.coroutines.launch

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldSheetContent(scaffoldSheetState: BottomSheetScaffoldState) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedDropdownIndex by remember {
        mutableIntStateOf(0)
    }


    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .heightIn(200.dp)
            .fillMaxWidth()
    ) {
        OutlinedIconButton(modifier = Modifier
            .widthIn(100.dp)
            .align(alignment = Alignment.TopCenter),
            shape = RoundedCornerShape(size = 12.dp), onClick = {
                expanded = !expanded
                coroutineScope.launch {
                    if (expanded) {
                        scaffoldSheetState.bottomSheetState.expand()
                    } else {
                        scaffoldSheetState.bottomSheetState.partialExpand()
                    }
                }

            }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = ModelType.entries.elementAt(selectedDropdownIndex).name,
                    style = Typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    ""
                )
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
            coroutineScope.launch {
                scaffoldSheetState.bottomSheetState.partialExpand()
            }
        }) {
            ModelType.entries.forEach { item ->
                DropdownMenuItem(

                    text = {
                        Text(text = item.name)
                    },

                    onClick = {
                        selectedDropdownIndex = ModelType.entries.indexOf(item)
                        expanded = false

                        coroutineScope.launch {
                            scaffoldSheetState.bottomSheetState.partialExpand()
                        }
                    })
            }
        }
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
@androidx.compose.ui.tooling.preview.Preview(
    device = "spec:width=1080px,height=2160px,dpi=440",
    showBackground = true, showSystemUi = true
)
fun ScaffoldSheetContentPreview() {
    ScaffoldSheetContent(rememberBottomSheetScaffoldState())
}