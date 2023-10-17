package com.example.compose_todo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todos::class], version = 1)
abstract class TodosDatabase: RoomDatabase(){
    abstract fun todosDao():TodosDao

    companion object{
        @Volatile private var todosDatabase:TodosDatabase? = null
        fun initDatabase(context: Context):TodosDatabase{
            synchronized(this){
                if(todosDatabase == null){
                    todosDatabase = Room.databaseBuilder(context,TodosDatabase::class.java,"todos").build()
                }
                return todosDatabase!!
            }
        }
    }
}