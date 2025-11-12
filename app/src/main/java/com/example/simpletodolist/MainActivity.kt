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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.example.simpletodolist.data.TodoDatabase
import com.example.simpletodolist.notifications.NotificationService
import com.example.simpletodolist.ui.screens.AppNavHost
import com.example.simpletodolist.ui.screens.TodoScreen
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val PREFS_NAME = "app_prefs"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

interface PermissionDelegate {
    fun requestExactAlarm()
    fun requestNotification()
}

enum class PermissionRequestState {
    NONE,
    NOTIFICATION_REQUESTED,
    ALARM_REQUESTED
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("Notification", "Разрешение на уведомления получено.")
            } else {
                Log.w("Notification", "Разрешение на уведомления отклонено.")
            }

                if (isNotificationPermissionGranted() && !isExactAlarmPermissionGranted()) {
                    requestExactAlarmPermission()
                    permissionState = PermissionRequestState.ALARM_REQUESTED
                } else {
                    permissionState = PermissionRequestState.NONE
                }
            }

    private val todoViewModel: TodoViewModel by viewModels()

    fun openAppSettings() {
        val intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                Log.d("Settings", "Открываем настройки уведомлений.")
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            }
            else -> {
                Log.d("Settings", "Открываем общие настройки приложения.")
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    Log.d("Notification", "Запрашиваем уведомления через системный диалог.")
                    requestPermissionLauncher.launch(permission)
                } else {
                    Log.w("Notification", "Разрешение на уведомления окончательно отклонено. Отправляем в настройки.")
                    openAppSettings()
                }
            }
        }
    }

    fun requestExactAlarmPermission() {
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

    fun markOnboardingAsComplete() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }

    private fun isOnboardingCompleted(): Boolean {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun hasAllReminderPermissions(context: Context): Boolean {
        val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val exactAlarmGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (this.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else {
            true
        }

        return notificationGranted && exactAlarmGranted
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isExactAlarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else {
            true
        }
    }

    private var permissionState = PermissionRequestState.NONE
    fun requestMissingPermissions() {
        if (!hasAllReminderPermissions(this)) {
            if (permissionState == PermissionRequestState.NONE) {
                if (!isNotificationPermissionGranted()) {
                    requestNotificationPermission()
                    permissionState = PermissionRequestState.NOTIFICATION_REQUESTED // Устанавливаем, что ждем уведомлений
                    return
                }

                if (!isExactAlarmPermissionGranted()) {
                    requestExactAlarmPermission()
                    permissionState = PermissionRequestState.ALARM_REQUESTED // Устанавливаем, что ждем будильника
                    return
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isComplete = remember { mutableStateOf(isOnboardingCompleted()) }
            SimpleTodoListTheme {
                AppNavHost(
                    isOnboardingCompleted = isComplete.value,
                    onOnboardingFinished = {
                        markOnboardingAsComplete()
                        isComplete.value = true
                    },
                    permissionDelegate = object : PermissionDelegate {
                        override fun requestExactAlarm() = requestExactAlarmPermission()
                        override fun requestNotification() = requestNotificationPermission()
                    },
                    todoViewModel = todoViewModel
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if (permissionState != PermissionRequestState.NONE) {

            if (hasAllReminderPermissions(this)) {
                permissionState = PermissionRequestState.NONE
                todoViewModel.dismissPermissionDialog()
                return
            }

            when (permissionState) {
                PermissionRequestState.NOTIFICATION_REQUESTED -> {
                    if (isNotificationPermissionGranted()) {
                        if (!isExactAlarmPermissionGranted()) {
                            requestExactAlarmPermission()
                            permissionState = PermissionRequestState.ALARM_REQUESTED
                        } else {
                            permissionState = PermissionRequestState.NONE
                        }
                    } else {
                        permissionState = PermissionRequestState.NONE
                    }
                }

                PermissionRequestState.ALARM_REQUESTED -> {
                    permissionState = PermissionRequestState.NONE
                }

                else -> {}
            }
        }
    }
}