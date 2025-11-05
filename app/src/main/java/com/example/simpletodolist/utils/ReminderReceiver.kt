package com.example.simpletodolist.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

const val EXTRA_TODO_ID = "extra_todo_id"
const val EXTRA_TODO_TITLE = "extra_todo_title"

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra(EXTRA_TODO_ID, -1)
        val todoTitle = intent.getStringExtra(EXTRA_TODO_TITLE)

        if (todoId != -1 && todoTitle != null) {
            Log.d("ReminderReceiver", "Напоминание сработало для ID: $todoId, Заголовок: $todoTitle")

            ReminderNotification.showNotification(context, todoId, todoTitle)

        } else {
            Log.e("ReminderReceiver", "Не удалось получить данные о задаче из Intent.")
        }
    }
}