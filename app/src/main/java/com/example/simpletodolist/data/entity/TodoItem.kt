package com.example.simpletodolist.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "is_assigned")
    val isAssigned: Boolean = false,

    @ColumnInfo(name = "date_of_completion")
    val completionDate: Long? = null,

    @ColumnInfo(name = "item_order")
    val orderIndex: Int = 0
)