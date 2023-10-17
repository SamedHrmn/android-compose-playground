package com.example.compose_todo.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose_todo.data.TodosDatabaseHelper
import com.example.compose_todo.view.AddTodoScreen
import com.example.compose_todo.view.TodoScreen
import com.example.compose_todo.viewmodel.TodoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()

    val todoViewModel = viewModel<TodoViewModel>(

        factory = object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TodoViewModel(TodosDatabaseHelper.getInstance(context = context)) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = Screen.TodoScreen.route){
        composable(Screen.TodoScreen.route){
            TodoScreen(navController = navController,todoViewModel)
        }
        composable(Screen.AddTodoScreen.route){
            AddTodoScreen(navController = navController,todoViewModel)
        }
    }

}

 sealed class Screen(val route:String){
     object TodoScreen: Screen("todo_screen")
     object AddTodoScreen:Screen("addTodo_screen")
}