package com.example.efficientdet_detector.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
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
import com.example.efficientdet_detector.ml.ModelType
import com.example.efficientdet_detector.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldSheetContent(
    scaffoldSheetState: BottomSheetScaffoldState,
    onItemSelected: (selectedModel: ModelType) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedDropdownIndex by remember {
        mutableIntStateOf(0)
    }


    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(200.dp)
    ) {

        OutlinedCard(modifier = Modifier
            .wrapContentWidth()
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
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = ModelType.entries.elementAt(selectedDropdownIndex).modelName,
                    style = Typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .padding(8.dp)
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
                        Text(text = item.modelName)
                    },

                    onClick = {
                        selectedDropdownIndex = ModelType.entries.indexOf(item)
                        expanded = false
                        onItemSelected(item)

                        coroutineScope.launch {
                            scaffoldSheetState.bottomSheetState.partialExpand()
                        }
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@androidx.compose.ui.tooling.preview.Preview(
    device = "spec:width=1080px,height=2160px,dpi=440",
    showBackground = true, showSystemUi = true
)
fun ScaffoldSheetContentPreview() {
    ScaffoldSheetContent(rememberBottomSheetScaffoldState(), onItemSelected = {})
}