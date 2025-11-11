package com.example.simpletodolist.di


import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import com.example.simpletodolist.data.dao.TodoDao
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DatabaseEntryPoint {
    fun todoDao(): TodoDao
}