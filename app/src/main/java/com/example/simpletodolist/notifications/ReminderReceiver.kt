package com.example.simpletodolist.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.simpletodolist.data.TodoDatabase
import com.example.simpletodolist.di.DatabaseEntryPoint
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val EXTRA_TODO_ID = "extra_todo_id"
const val EXTRA_TODO_TITLE = "extra_todo_title"

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra(EXTRA_TODO_ID, -1)
        val todoTitle = intent.getStringExtra(EXTRA_TODO_TITLE)

        if (todoId != -1 && todoTitle != null) {
            Log.d("ReminderReceiver", "Напоминание сработало для ID: $todoId, Заголовок: $todoTitle")

            ReminderNotification.showNotification(context, todoId, todoTitle)

            val entryPoint = EntryPoints.get(
                context.applicationContext,
                DatabaseEntryPoint::class.java
            )
            val todoDao = entryPoint.todoDao()

            CoroutineScope(Dispatchers.IO).launch {
                val item = todoDao.getItem(todoId)

                if (item != null && !item.isCompleted) {
                    val updatedItem = item.copy(
                        lastActionTimestamp = System.currentTimeMillis()
                    )

                    todoDao.update(updatedItem)
                    Log.d("ReminderReceiver", "Задача ID: $todoId обновлена в БД.")
                }
            }

        } else {
            Log.e("ReminderReceiver", "Не удалось получить данные о задаче из Intent.")
        }
    }
}