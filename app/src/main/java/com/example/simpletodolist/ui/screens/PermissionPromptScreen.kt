package com.example.simpletodolist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme

@Composable
fun PermissionPromptScreen(
    innerPadding: PaddingValues,
    onGrantPermission: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Text(text = "One-time setup", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "To use reminders effectively, please grant notification permissions.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 15.dp)
        )

        Spacer(Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onDecline, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outlineVariant)) {
                Text("Decline", color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.size(10.dp))
            Button(onClick = onGrantPermission) {
                Text("Grant permission", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionPromptScreen() {
    SimpleTodoListTheme {
        PermissionPromptScreen(
            innerPadding = PaddingValues(0.dp),
            onGrantPermission = { },
            onDecline = { }
        )
    }
}