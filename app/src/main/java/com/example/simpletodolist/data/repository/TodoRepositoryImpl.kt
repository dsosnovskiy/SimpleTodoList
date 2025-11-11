package com.example.simpletodolist.data.repository

import com.example.simpletodolist.data.dao.TodoDao
import com.example.simpletodolist.data.model.TodoItem
import com.example.simpletodolist.notifications.NotificationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao,
    private val notificationService: NotificationService
) : TodoRepository {

    // --- Flows ---
    override fun getUnassignedItemsFlow(): Flow<List<TodoItem>> = todoDao.getUnassignedItems()
    override fun getAssignedItemsFlow(): Flow<List<TodoItem>> = todoDao.getAssignedItems()
    override fun getCompletedItemsFlow(): Flow<List<TodoItem>> = todoDao.getCompletedItems()

    // --- CRUD ---

    override suspend fun getItem(itemId: Int): TodoItem? {
        return todoDao.getItem(itemId)
    }

    override suspend fun insertTodo(title: String, reminderTime: Long?): Int {
        val newTodo = TodoItem(
            title = title,
            orderIndex = null,
            lastActionTimestamp = System.currentTimeMillis(),
            reminderTime = reminderTime
        )
        val newRowId = todoDao.insert(newTodo)
        val newId = newRowId.toInt()

        if (reminderTime != null) {
            notificationService.scheduleReminder(
                id = newId,
                title = title,
                timeMillis = reminderTime,
            )
        }
        return newId
    }

    override suspend fun updateTodo(item: TodoItem) {
        val currentTodo = todoDao.getItem(item.id)
        currentTodo?.let {
            notificationService.cancelReminder(item.id)
        }

        todoDao.update(item)

        if (item.reminderTime != null) {
            notificationService.scheduleReminder(
                id = item.id,
                title = item.title,
                timeMillis = item.reminderTime
            )
        }
    }

    override suspend fun updateTodoContent(
        todoId: Int,
        newTitle: String,
        reminderTime: Long?
    ) {
        val currentTodo = todoDao.getItem(todoId) ?: return

        val updatedItem = currentTodo.copy(
            title = newTitle,
            reminderTime = reminderTime,
            lastActionTimestamp = System.currentTimeMillis(),
        )

        notificationService.cancelReminder(todoId)
        if (updatedItem.reminderTime != null) {
            notificationService.scheduleReminder(
                id = updatedItem.id,
                title = updatedItem.title,
                timeMillis = updatedItem.reminderTime
            )
        }

        todoDao.update(updatedItem)
    }

    override suspend fun deleteTodo(itemId: Int) {
        notificationService.cancelReminder(itemId)
        todoDao.delete(itemId)
    }

    override suspend fun toggleCompletionStatus(todoId: Int) {
        val item = todoDao.getItem(todoId) ?: return

        val newStatus = !item.isCompleted
        var newReminderTime: Long? = item.reminderTime
        var newCompletionDate: Long? = null

        if (newStatus) {
            newCompletionDate = System.currentTimeMillis()
            if (item.reminderTime != null) {
                notificationService.cancelReminder(todoId)
            }
            newReminderTime = null
        }

        todoDao.update(
            item.copy(
                isCompleted = newStatus,
                completionDate = newCompletionDate,
                orderIndex = null,
                lastActionTimestamp = System.currentTimeMillis(),
                reminderTime = newReminderTime
            )
        )
    }

    override suspend fun toggleAssignedStatus(todoId: Int) {
        val item = todoDao.getItem(todoId) ?: return

        val newStatus = !item.isAssigned

        todoDao.update(
            item.copy(
                isAssigned = newStatus,
                orderIndex = null,
                lastActionTimestamp = System.currentTimeMillis(),
            )
        )
    }

    override suspend fun updateOrder(newOrderList: List<TodoItem>) {
        val itemsToUpdate = newOrderList.mapIndexed { index, item ->
            val newManualIndex = newOrderList.size - index.toLong()
            item.copy(
                orderIndex = newManualIndex,
            )
        }

        if (itemsToUpdate.isNotEmpty()) {
            todoDao.update(itemsToUpdate)
        }
    }
}