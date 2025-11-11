package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getUnassignedItemsFlow(): Flow<List<TodoItem>>
    fun getAssignedItemsFlow(): Flow<List<TodoItem>>
    fun getCompletedItemsFlow(): Flow<List<TodoItem>>

    suspend fun getItem(itemId: Int): TodoItem?
    suspend fun insertTodo(title: String, reminderTime: Long?): Int
    suspend fun updateTodo(item: TodoItem)
    suspend fun updateTodoContent(
        todoId: Int,
        newTitle: String,
        reminderTime: Long?
    )
    suspend fun deleteTodo(itemId: Int)

    suspend fun toggleCompletionStatus(todoId: Int)
    suspend fun toggleAssignedStatus(todoId: Int)
    suspend fun updateOrder(newOrderList: List<TodoItem>)
}