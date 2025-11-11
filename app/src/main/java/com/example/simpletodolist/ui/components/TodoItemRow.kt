package com.example.simpletodolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpletodolist.data.model.TodoItem
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.utils.toFormattedDateTimeFromMillis
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

    val formattedCompletionDate = todo.completionDate.toFormattedDateTimeFromMillis()
    val formatedReminderTime = todo.reminderTime.toFormattedDateTimeFromMillis()

    val clickModifier = if (isSelectionModeEnabled) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                onSelectionClick(todo.id)
            },
        )
    } else {
        Modifier.combinedClickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                isExpanded = !isExpanded
            },
            onLongClick = {
                onLongClick(todo.id)
            }
        )
    }

    val draggableHandleModifier = if (isSelectionModeEnabled && reorderableScope != null) {
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
            .then(draggableHandleModifier)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp),
            )
            .then(
                if (todo.reminderTime != null && todo.reminderTime < System.currentTimeMillis()) {
                    Modifier.border(width = 1.dp, shape = RoundedCornerShape(15.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                } else {
                    Modifier
                }
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.width(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isSelectionModeEnabled) {
                if (!todo.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        tint = dragIconColor,

                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember {MutableInteractionSource()}
                            ) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                            }.size(25.dp),
                    )
                }
            } else {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = {
                        onCheckedChange()
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    },
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
                fontWeight = FontWeight.SemiBold,
                maxLines = maxTextLines,
                overflow = TextOverflow.Ellipsis,
            )
            if (todo.isCompleted) {
                Text(
                    text = "Completed: $formattedCompletionDate",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (todo.reminderTime != null && todo.reminderTime > System.currentTimeMillis()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp).offset(y = (-1).dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Reminder: $formatedReminderTime",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (todo.reminderTime != null && todo.reminderTime < System.currentTimeMillis()){
                Text(
                    text = "Time to complete the task!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (isSelectionModeEnabled) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = {
                    onSelectionClick(todo.id)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                },
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
            todo = TodoItem(1, "Задача 1", false, true) ,
            onCheckedChange = { },
            isSelectionModeEnabled = false,
            isSelected = false,
            onLongClick = { _ -> },
            onSelectionClick = { _ ->},
            reorderableScope = null
        )
    }
}