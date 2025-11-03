package com.example.simpletodolist.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.example.simpletodolist.ui.theme.deleteButtonColor

@Composable
fun ActionButton(onClick: () -> Unit = {}, isSelectionModeEnabled: Boolean, selectedCount: Int) {
    val isActive = !isSelectionModeEnabled || (isSelectionModeEnabled && selectedCount > 0)
    FloatingActionButton(
        containerColor = if (!isSelectionModeEnabled) {
            MaterialTheme.colorScheme.primary
        } else if (!isActive) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            deleteButtonColor
        },
        contentColor = Color.White,
        onClick = if (isActive) onClick else { -> },
    ) {
        if (isSelectionModeEnabled) {
            Icon(Icons.Filled.Delete, null)
        } else {
            Icon(Icons.Filled.Add, null)
        }
    }
}