package com.example.compose_todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodosDao {
    @Query("SELECT * FROM todos")
    fun getAll():Flow<List<Todos>>
    @Delete
    suspend fun delete(todos: Todos)

    @Insert
    suspend fun insert(todos: Todos)

    @Query("UPDATE todos SET is_completed =:isCompleted WHERE id =:todoId")
    suspend fun update(isCompleted:Boolean,todoId:Int)
}