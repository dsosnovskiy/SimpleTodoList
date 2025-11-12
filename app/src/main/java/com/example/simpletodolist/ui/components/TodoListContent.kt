package com.example.simpletodolist.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simpletodolist.R
import com.example.simpletodolist.data.model.TodoItem
import com.example.simpletodolist.ui.theme.SimpleTodoListTheme
import com.example.simpletodolist.ui.viewmodel.TodoListState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListContent(
    state: TodoListState,

    // Selection Mode off
    onToggle: (Int) -> Unit,
    onAdd: (String, Long?) -> Unit,
    onRemove: (Int) -> Unit,
    onEdit: (Int, String, Long?) -> Unit,
    onAssign: (Int) -> Unit,

    // Selection Mode on
    onLongPress: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onRemoveSelected: () -> Unit,
    onToggleSelection: (Int) -> Unit,
    onSaveNewOrder: (List<TodoItem>) -> Unit,

    onCheckPermissions: (() -> Boolean) -> Boolean,
    showPermissionDialog: Boolean,
    onDismissPermissionDialog: () -> Unit,
    onRequestMissingPermissions: () -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newTodoText by remember { mutableStateOf("") }
    var showCompletedTodos by remember { mutableStateOf(true) }

    // Selection Mode
    val isSelectionModeEnabled = state.isSelectionModeEnabled
    val selectedIds = state.selectedTodoIds
    val hapticFeedback = LocalHapticFeedback.current

    // Reorderable
    var localAssignedTodos by remember { mutableStateOf(state.assignedTodos) }
    var localUnassignedTodos by remember { mutableStateOf(state.unassignedTodos) }
    LaunchedEffect(state.assignedTodos) {
        if (localAssignedTodos != state.assignedTodos) {
            localAssignedTodos = state.assignedTodos
        }
    }
    LaunchedEffect(state.unassignedTodos) {
        if (localUnassignedTodos != state.unassignedTodos) {
            localUnassignedTodos = state.unassignedTodos
        }
    }

    val lazyListState = rememberLazyListState()

    val assignedReorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        localAssignedTodos = localAssignedTodos.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    val unassignedReorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val assignedCount = localAssignedTodos.size
        val fromLocalIndex = from.index - assignedCount
        val toLocalIndex = to.index - assignedCount

        if (fromLocalIndex >= 0 && toLocalIndex >= 0 && toLocalIndex <= localUnassignedTodos.size) {
            localUnassignedTodos = localUnassignedTodos.toMutableList().apply {
                add(toLocalIndex, removeAt(fromLocalIndex))
            }

            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
    }

    // SwipeToReveal
    var currentRevealedItemId by remember { mutableStateOf<Int?>(null) }
    val onReveal: (Int) -> Unit = { itemId -> currentRevealedItemId = itemId }
    val onCollapsed: () -> Unit = { currentRevealedItemId = null }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                isSelectionModeEnabled = isSelectionModeEnabled,
                selectedCount = selectedIds.size,
                onClearSelection = onClearSelection,
                onSelectAll = onSelectAll
            )
        },
        floatingActionButton = {
            ActionButton(
                onClick = {
                    if (isSelectionModeEnabled) {
                        onRemoveSelected()
                    } else {
                        showAddDialog = true
                    } },
                isSelectionModeEnabled = isSelectionModeEnabled,
                selectedCount = selectedIds.size
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyListState,
            contentPadding = innerPadding,
        ) {
            if (state.assignedTodos.isNotEmpty()) {
                items(localAssignedTodos, key = { it.id }) { todo ->
                    ReorderableItem(
                        assignedReorderableState,
                        key = todo.id,
                    ) { isDragging ->
                        LaunchedEffect(isDragging) {
                            if (!isDragging) {
                                if (localAssignedTodos != state.assignedTodos) {
                                    val newOrder = localAssignedTodos + localUnassignedTodos
                                    onSaveNewOrder(newOrder)
                                }
                            }
                        }
                        TodoItemRevealWrapper(
                            todo = todo,
                            onToggle = onToggle,
                            onRemove = onRemove,
                            onEdit = onEdit,
                            onAssign = onAssign,

                            isSelectionModeEnabled = isSelectionModeEnabled,
                            isSelected = selectedIds.contains(todo.id),
                            onLongClick = onLongPress,
                            onSelectionClick = onToggleSelection,

                            reorderableScope = this,
                            isDragging = isDragging,

                            onCheckPermissions = onCheckPermissions,

                            currentRevealedItemId = currentRevealedItemId,
                            onReveal = onReveal,
                            onCollapsed = onCollapsed,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
            if (state.unassignedTodos.isNotEmpty()) {
                items(localUnassignedTodos, key = { it.id }) { todo ->
                    ReorderableItem(
                        unassignedReorderableState,
                        key = todo.id,
                    ) { isDragging ->
                        LaunchedEffect(isDragging) {
                            if (!isDragging) {
                                if (localUnassignedTodos != state.unassignedTodos) {
                                    val newOrder = localAssignedTodos + localUnassignedTodos
                                    onSaveNewOrder(newOrder)
                                }
                            }
                        }
                        TodoItemRevealWrapper(
                            todo = todo,
                            onToggle = onToggle,
                            onRemove = onRemove,
                            onEdit = onEdit,
                            onAssign = onAssign,

                            isSelectionModeEnabled = isSelectionModeEnabled,
                            isSelected = selectedIds.contains(todo.id),
                            onLongClick = onLongPress,
                            onSelectionClick = onToggleSelection,

                            reorderableScope = this,
                            isDragging = isDragging,

                            onCheckPermissions = onCheckPermissions,

                            currentRevealedItemId = currentRevealedItemId,
                            onReveal = onReveal,
                            onCollapsed = onCollapsed,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
            if (state.completedTodos.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    showCompletedTodos = !showCompletedTodos
                                }
                            )
                            .padding(top = 5.dp, bottom = 5.dp, start = 20.dp),
                    ) {
                        val iconRes =
                            if (showCompletedTodos) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant                           )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = "Completed ${state.completedTodos.size}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (showCompletedTodos) {
                    items(state.completedTodos, key = { "completed-${it.id}" }) { todo ->
                        TodoItemRevealWrapper(
                            todo = todo,
                            onToggle = onToggle,
                            onRemove = onRemove,
                            onEdit = onEdit,
                            onAssign = onAssign,

                            isSelectionModeEnabled = isSelectionModeEnabled,
                            isSelected = selectedIds.contains(todo.id),
                            onLongClick = onLongPress,
                            onSelectionClick = onToggleSelection,

                            reorderableScope = null,
                            isDragging = false,

                            currentRevealedItemId = currentRevealedItemId,
                            onReveal = onReveal,
                            onCollapsed = onCollapsed,

                            onCheckPermissions = onCheckPermissions,

                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            TextDialog(
                currentText = newTodoText,
                onTextChanged = {
                    newTodoText = it.trimStart().replace(Regex("[\n\r]{2,}"), "\n")                                },
                onSave = { millis ->
                    val cleanedTextForSave = newTodoText.trim()

                    if (cleanedTextForSave.isNotBlank()) {
                        onAdd(cleanedTextForSave, millis)
                        newTodoText = ""
                        showAddDialog = false
                    }
                },
                onDismiss = {
                    newTodoText = ""
                    showAddDialog = false
                },
                onReminderClicked = onCheckPermissions
            )
        }

        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = {
                    onDismissPermissionDialog()
                },
                title = { Text("Permissions Required", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                text = { Text("To set reminders, please grant access to notifications and exact alarms.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                confirmButton = {
                    Button(onClick = {
                        onRequestMissingPermissions()
                        onDismissPermissionDialog()
                    }) {
                        Text("Grant Permission", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                dismissButton = {
                    Button(onClick = onDismissPermissionDialog, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outlineVariant)) {
                        Text("Decline", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true)
@Composable
fun TodoListContentPreview() {
    val allTodos = listOf(
        TodoItem(1, "Задача 1: Незавершенная", false, true),
        TodoItem(2, "Задача 2: Завершена", true),
        TodoItem(3, "Задача 3: Незавершенная", false),
        TodoItem(4, "Задача 4: Завершена", true),
        TodoItem(5, "Задача 5: Завершена", true),
        TodoItem(6, "Задача 6: Незавершенная", false, false),
    )

    val uncompleted = allTodos.filter { !it.isCompleted }
    val completed = allTodos.filter { it.isCompleted }

    val dummyState = TodoListState(
        todos = allTodos,
        uncompletedTodos = uncompleted,
        completedTodos = completed,
        isSelectionModeEnabled = false,
        selectedTodoIds = emptySet()
    )

    SimpleTodoListTheme {
        TodoListContent(
            state = dummyState,
            onToggle = { },
            onAdd = { _, _ -> },
            onRemove = { },
            onEdit = { _, _, _-> },
            onLongPress = { _ ->},
            onToggleSelection = { _ ->},
            onClearSelection = { },
            onSelectAll = { },
            onRemoveSelected = { },
            onSaveNewOrder = { },
            onAssign = { },

            onCheckPermissions = { _ -> true },
            showPermissionDialog = false,
            onDismissPermissionDialog = {},
            onRequestMissingPermissions = {}
        )
    }
}