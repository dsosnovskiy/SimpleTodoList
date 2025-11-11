package com.example.simpletodolist.di

import android.content.Context
import androidx.room.Room
import com.example.simpletodolist.data.TodoDatabase
import com.example.simpletodolist.data.dao.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "simple_todo_list_db"
        )
            .build()
    }

    @Provides
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao()
    }
}