package com.example.compose_todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todos(
    @PrimaryKey(autoGenerate = true) val id:Int?,
    @ColumnInfo(name = "title") val title:String,
    @ColumnInfo(name = "category") val category:Int,
    @ColumnInfo(name = "date") val date:String,
    @ColumnInfo(name="time") val time:String,
    @ColumnInfo(name = "notes") val notes:String,
    @ColumnInfo(name = "is_completed") val isCompleted:Boolean,
)
