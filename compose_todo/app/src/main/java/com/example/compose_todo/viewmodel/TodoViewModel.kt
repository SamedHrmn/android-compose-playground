package com.example.compose_todo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_todo.data.Todos
import com.example.compose_todo.data.TodosDatabaseHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(private val todosDatabaseHelper: TodosDatabaseHelper) : ViewModel(){

     val todoState = flow {
         try {
             emit(TodoUiState.Loading)
             todosDatabaseHelper.getAllTodo().collect{

                 emit(TodoUiState.Loaded(it))
             }
         }catch (e:Exception){
            emit(TodoUiState.Error(e.message))
         }
     }.stateIn(viewModelScope, SharingStarted.Lazily, TodoUiState.Loading)


     fun addTodo(todo:Todos){
         viewModelScope.launch {
             todosDatabaseHelper.addTodo(todo)
         }
     }
    fun deleteTodo(todo:Todos){
        viewModelScope.launch {
            todosDatabaseHelper.deleteTodo(todo)
        }
    }

    fun updateTodoWhenChecked(todo:Todos){
        viewModelScope.launch {
            todosDatabaseHelper.updateTodo(todo)
        }
    }

}

sealed class TodoUiState {
    object Loading : TodoUiState()
    data class Loaded(val todos: List<Todos>) : TodoUiState()
    data class Error(val message: String?) : TodoUiState()
}