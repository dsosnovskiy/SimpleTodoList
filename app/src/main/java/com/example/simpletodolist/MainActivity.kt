package com.example.simpletodolist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpletodolist.data.TodoDatabase
import com.example.simpletodolist.data.service.NotificationService
import com.example.simpletodolist.ui.screens.TodoScreen
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.ui.viewmodel.TodoViewModelFactory

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("Notification", "Разрешение на уведомления получено.")
            } else {
                Log.w("Notification", "Разрешение на уведомления отклонено.")
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val todoDao = TodoDatabase.getDatabase(applicationContext).todoDao()

        val notificationService = NotificationService(applicationContext)

        val factory = TodoViewModelFactory(
            todoDao = todoDao,
            notificationService = notificationService
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Tiramisu = API 33
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            SimpleTodoListTheme {
                TodoScreen(factory = factory)
            }
        }
    }
}