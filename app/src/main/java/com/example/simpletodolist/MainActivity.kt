package com.example.simpletodolist

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.simpletodolist.data.TodoDatabase
import com.example.simpletodolist.notifications.NotificationService
import com.example.simpletodolist.ui.screens.TodoScreen
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("Notification", "Разрешение на уведомления получено.")
            } else {
                Log.w("Notification", "Разрешение на уведомления отклонено.")
            }
        }

    private val todoViewModel: TodoViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Requesting permission for precise alarms (API 31+)
        checkAndRequestExactAlarmPermission()

        // Requesting permission for Push-notifications (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Tiramisu = API 33
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            SimpleTodoListTheme {
                TodoScreen(viewModel = todoViewModel)
            }
        }
    }

    private fun checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {
                Log.i("Notification", "Запрашиваем разрешение SCHEDULE_EXACT_ALARM у пользователя.")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
        }
    }
}