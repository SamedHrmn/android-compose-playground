package com.example.compose_todo.data
import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.flow.Flow

class TodosDatabaseHelper(context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: TodosDatabaseHelper? = null
        fun getInstance(context: Context): TodosDatabaseHelper {
            synchronized(this) {
                if (instance == null) {
                    instance = TodosDatabaseHelper(context)

                }
                return instance!!
            }
        }
    }

    private var db: TodosDatabase = TodosDatabase.initDatabase(context = context)

    suspend fun addTodo(todos: Todos) {
        db.todosDao().insert(todos)
    }

    suspend fun deleteTodo(todos: Todos) {
        db.todosDao().delete(todos)
    }

    suspend fun updateTodo(todos:Todos){
        db.todosDao().update(todoId = todos.id!!, isCompleted = todos.isCompleted)
    }

    fun getAllTodo(): Flow<List<Todos>> {
        return db.todosDao().getAll()
    }

}