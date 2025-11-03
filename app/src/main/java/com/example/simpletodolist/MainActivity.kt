package com.example.simpletodolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.simpletodolist.data.TodoDatabase
import com.example.simpletodolist.ui.screens.TodoScreen
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.ui.viewmodel.TodoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val todoDao = TodoDatabase.getDatabase(applicationContext).todoDao()
        val factory = TodoViewModelFactory(todoDao)
        setContent {
            SimpleTodoListTheme {
                TodoScreen(factory = factory)
            }
        }
    }
}