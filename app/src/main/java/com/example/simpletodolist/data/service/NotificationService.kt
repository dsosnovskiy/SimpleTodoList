// com.example.simpletodolist.data.service/NotificationService.kt
package com.example.simpletodolist.data.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.simpletodolist.utils.EXTRA_TODO_ID
import com.example.simpletodolist.utils.EXTRA_TODO_TITLE
import com.example.simpletodolist.utils.ReminderReceiver
import java.util.concurrent.TimeUnit

class NotificationService(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(id: Int, title: String, timeMillis: Long) {
        if (timeMillis <= System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1)) {
            Log.w("NotificationService", "Время напоминания уже прошло или слишком близко. Пропуск.")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("NotificationService", "Нет разрешения SCHEDULE_EXACT_ALARM. Пользователь должен дать его вручную.")

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)

                scheduleInexactReminder(id, title, timeMillis)
                return
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_TODO_ID, id)
            putExtra(EXTRA_TODO_TITLE, title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeMillis,
                pendingIntent
            )
            Log.d("NotificationService", "Точное напоминание запланировано на $timeMillis для ID: $id")
        } catch (e: SecurityException) {
            Log.e("NotificationService", "Ошибка безопасности при планировании точного будильника: ${e.message}")
        }
    }

    private fun scheduleInexactReminder(id: Int, title: String, timeMillis: Long) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_TODO_ID, id)
            putExtra(EXTRA_TODO_TITLE, title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            timeMillis,
            pendingIntent
        )
        Log.w("NotificationService", "Не удалось запланировать точное. Запланировано неточное напоминание на $timeMillis для ID: $id")
    }

    fun cancelReminder(id: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            Log.d("NotificationService", "Напоминание отменено для ID: $id")
        }
    }
}