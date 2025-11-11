package com.example.simpletodolist.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.concurrent.TimeUnit

class NotificationService(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(id: Int, title: String, timeMillis: Long) {
        if (timeMillis <= System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1)) {
            Log.w("NotificationService", "Время напоминания уже прошло или слишком близко. Пропуск.")
            return
        }

        var useExactAlarm = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            useExactAlarm = alarmManager.canScheduleExactAlarms()
            if (!useExactAlarm) {
                Log.w("NotificationService", "Нет разрешения SCHEDULE_EXACT_ALARM. Используется неточный будильник.")
            }
        } else {
            useExactAlarm = true
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
            if (useExactAlarm) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeMillis,
                    pendingIntent
                )
                Log.d("NotificationService", "Точное напоминание запланировано на $timeMillis для ID: $id")
            } else {
                scheduleInexactReminder(id, title, timeMillis)
            }
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