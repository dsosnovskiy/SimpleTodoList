package com.example.simpletodolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simpletodolist.data.dao.TodoDao
import com.example.simpletodolist.data.model.TodoItem
import javax.inject.Singleton

@Singleton
@Database(
    entities = [TodoItem::class],
    exportSchema = true,
    version = 1,
)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

}