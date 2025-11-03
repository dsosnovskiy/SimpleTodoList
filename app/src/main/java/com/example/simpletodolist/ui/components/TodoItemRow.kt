package com.example.simpletodolist.ui.components

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpletodolist.data.entity.TodoItem
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.ui.theme.pinButtonColor
import com.example.simpletodolist.utils.toFormattedDateTime
import sh.calvin.reorderable.ReorderableCollectionItemScope


@Composable
fun TodoItemRow(
    todo: TodoItem,
    onCheckedChange: () -> Unit,

    isSelectionModeEnabled: Boolean,
    isSelected: Boolean,
    onLongClick: (Int) -> Unit,
    onSelectionClick: (Int) -> Unit,

    reorderableScope: ReorderableCollectionItemScope??,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val hapticFeedback = LocalHapticFeedback.current

    var isExpanded by remember { mutableStateOf(false) }
    val maxTextLines = if (isExpanded) Int.MAX_VALUE else 4

    val formattedDate = todo.completionDate.toFormattedDateTime()

    val clickModifier = if (isSelectionModeEnabled) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = { onSelectionClick(todo.id) }
        )
    } else {
        Modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                isExpanded = !isExpanded
            },
            onLongClick = { onLongClick(todo.id) }
        )
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val dragIconColor = if (todo.isAssigned) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.outline
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(clickModifier)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(15.dp),
            )
            .padding(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.width(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isSelectionModeEnabled) {
                if (!todo.isCompleted) {
                    val handleModifier = if (reorderableScope != null) {
                        with(reorderableScope) {
                            Modifier.draggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                },
                            )
                        }
                    } else {
                        Modifier
                    }
                    IconButton(
                        modifier = handleModifier,
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = dragIconColor,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }
            } else {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onCheckedChange() },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(3.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = todo.title,
                fontSize = 17.sp,
                color = if (todo.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                ),
                maxLines = maxTextLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (todo.isCompleted) {
                Text(
                    text = "Выполнено: $formattedDate",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (isSelectionModeEnabled) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionClick(todo.id) },
                colors = CheckboxDefaults.colors(
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
        } else if (!todo.isCompleted && todo.isAssigned) {
            Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 12.dp).size(18.dp)
            )
        }
    }
}

@Preview
@Composable
fun TodoItemRowPreview() {
    SimpleTodoListTheme {
        TodoItemRow(
            todo = TodoItem(1, "Задача 1", true, true) ,
            onCheckedChange = { },
            isSelectionModeEnabled = false,
            isSelected = false,
            onLongClick = { _ -> },
            onSelectionClick = { _ ->},
            reorderableScope = null
        )
    }
}