package com.example.simpletodolist.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.simpletodolist.data.entity.TodoItem
import com.example.simpletodolist.ui.theme.deleteButtonColor
import com.example.simpletodolist.ui.theme.editButtonColor
import com.example.simpletodolist.ui.theme.topBarColor
import com.example.simpletodolist.ui.theme.pinButtonColor
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun TodoItemRevealWrapper(
    todo: TodoItem,
    onToggle: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onEdit: (Int, String) -> Unit,
    onAssign: (Int) -> Unit,

    isSelectionModeEnabled: Boolean,
    isSelected: Boolean,
    onLongClick: (Int) -> Unit,
    onSelectionClick: (Int) -> Unit,

    currentRevealedItemId: Int?,
    onReveal: (Int) -> Unit,
    onCollapsed: () -> Unit,

    reorderableScope: ReorderableCollectionItemScope?,
    isDragging: Boolean,

    modifier: Modifier = Modifier,
) {
    val currentItem by rememberUpdatedState(todo)
    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

    var showEditDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(currentItem.title) }

    val isRevealed = todo.id == currentRevealedItemId
    val isSwipeEnabled = !isDragging && !isSelectionModeEnabled

    if (showEditDialog) {
        TextDialog(
            currentText = editText,
            onTextChanged = {
                editText = it.trimStart().replace(Regex("[\n\r]{2,}"), "\n")
            },
            onAdd = {
                val cleanedTextForSave = editText.trim()

                if (cleanedTextForSave.isNotBlank()) {
                    onEdit(currentItem.id, editText)
                    editText = ""
                    showEditDialog = false
                    onCollapsed()
                }
            },
            onDismiss = {
                editText = ""
                showEditDialog = false
                onCollapsed()
            }
        )
    }
    Box(
        modifier = modifier
            .padding(horizontal = 7.dp)
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .shadow(elevation)
    ) {
        if (isSelectionModeEnabled) {
            TodoItemRow(
                todo = todo,
                onCheckedChange = { },
                isSelectionModeEnabled = true,
                isSelected = isSelected,
                onLongClick = onLongClick,
                onSelectionClick = onSelectionClick,
                reorderableScope = reorderableScope
            )
        } else if (!isSwipeEnabled) {
            TodoItemRow(
                todo = todo,
                onCheckedChange = { onToggle(todo.id) },
                isSelectionModeEnabled = false,
                isSelected = false,
                onLongClick = onLongClick,
                onSelectionClick = onSelectionClick,
                reorderableScope = reorderableScope
            )
        } else {
            SwipeRevealContainer(
                isRevealed = isRevealed,
                onExpanded = { onReveal(todo.id) },
                onCollapsed = onCollapsed,
                actions = {
                    val assignedTodoСontentDescription = if (!todo.isAssigned) "Закрепить" else "Открепить"
                    if (!todo.isCompleted) {
                        ActionIcon(
                            onClick = {
                                onAssign(currentItem.id)
                                onCollapsed()
                            },
                            backgroundColor = pinButtonColor,
                            icon = Icons.Default.PushPin,
                            contentDescription = assignedTodoСontentDescription,
                            modifier = Modifier.fillMaxHeight()
                        )
                        ActionIcon(
                            onClick = {
                                showEditDialog = true
                                editText = currentItem.title
                            },
                            backgroundColor = editButtonColor,
                            icon = Icons.Default.Edit,
                            contentDescription = "Изменить",
                            modifier = Modifier.fillMaxHeight()
                        )
                        ActionIcon(
                            onClick = {
                                onRemove(currentItem.id)
                                onCollapsed()
                            },
                            backgroundColor = deleteButtonColor,
                            icon = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            modifier = Modifier.fillMaxHeight()
                        )
                    } else {
                        ActionIcon(
                            onClick = {
                                onRemove(currentItem.id)
                                onCollapsed()
                            },
                            backgroundColor = deleteButtonColor,
                            icon = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                },
                content = {
                    TodoItemRow(
                        todo = todo,
                        onCheckedChange = {
                            onToggle(todo.id)
                            onCollapsed()
                        },
                        isSelectionModeEnabled = false,
                        isSelected = false,
                        onLongClick = onLongClick,
                        onSelectionClick = onSelectionClick,
                        reorderableScope = reorderableScope
                    )
                }
            )
        }
    }
}