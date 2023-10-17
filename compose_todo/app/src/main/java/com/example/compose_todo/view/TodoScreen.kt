package com.example.compose_todo.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.compose_todo.R
import com.example.compose_todo.common.TodoCard
import com.example.compose_todo.common.TodoPrimaryButton
import com.example.compose_todo.util.Screen
import com.example.compose_todo.viewmodel.TodoUiState
import com.example.compose_todo.viewmodel.TodoViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoScreen(navController: NavController, viewModel: TodoViewModel) {
    val todoState by viewModel.todoState.collectAsState()

    Surface(
        modifier = Modifier
            .background(color = Color(0xFFF1F5F9))
            .padding(top = 12.dp, bottom = 24.dp), color = Color(0xFFF1F5F9)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Spacer(modifier = Modifier)
            when (val state = todoState) {
                is TodoUiState.Loading -> CircularProgressIndicator()
                is TodoUiState.Error -> Text(
                    text = stringResource(R.string.something_went_wrong)
                )

                is TodoUiState.Loaded -> {
                    val todos = state.todos

                    if (todos.isEmpty()) {
                        Text(text = stringResource(R.string.there_is_no_todo_yet))
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            state = rememberLazyListState(),
                        ) {
                            items(
                                count = todos.size,
                                key = {
                                    todos[it].id!!
                                },
                            ) { index ->

                                val swipeToDismissState =
                                    rememberDismissState(confirmValueChange = {
                                        if (it == DismissValue.DismissedToStart) {
                                            viewModel.deleteTodo(todo = todos[index])
                                        }
                                        true
                                    })

                                SwipeToDismiss(
                                    state = swipeToDismissState,
                                    modifier = Modifier.animateItemPlacement(),
                                    directions = setOf(DismissDirection.EndToStart),
                                    background = {
                                        var cardShape = RoundedCornerShape(0.dp)
                                        if (index == 0) {
                                            cardShape =
                                                RoundedCornerShape(
                                                    topStart = 12.dp,
                                                    topEnd = 12.dp
                                                )
                                        } else if (index == todos.size - 1) {
                                            cardShape = RoundedCornerShape(
                                                bottomStart = 12.dp,
                                                bottomEnd = 12.dp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = if (index == 0) 12.dp else 0.dp)
                                                .shadow(
                                                    4.dp,
                                                    shape = cardShape,
                                                    clip = true
                                                )
                                                .background(color = Color.Red),
                                            contentAlignment = Alignment.CenterEnd,
                                        ) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = null,
                                                Modifier
                                                    .size(48.dp)
                                                    .padding(end = 16.dp)
                                            )
                                        }
                                    },
                                    dismissContent = {
                                        TodoCard(
                                            todo = todos[index],
                                            isFirst = index == 0,
                                            isLast = index == todos.size - 1,
                                            onChecked = {
                                                viewModel.updateTodoWhenChecked(
                                                    todos[index].copy(
                                                        isCompleted = it
                                                    )
                                                )
                                            }
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
            TodoPrimaryButton(
                textId = R.string.add_new_task,
                onClick = {
                    navController.navigate(Screen.AddTodoScreen.route)
                },
            )
        }
    }
}


