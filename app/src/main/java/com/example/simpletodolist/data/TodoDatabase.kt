package com.example.simpletodolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simpletodolist.data.dao.TodoDao
import com.example.simpletodolist.data.entity.TodoItem

@Database(entities = [TodoItem::class], version = 1, exportSchema = true)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "simple_todo_list_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}