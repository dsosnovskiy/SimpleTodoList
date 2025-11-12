package com.example.simpletodolist.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.dao.TodoDao // ⬅️ Теперь используем DAO
import com.example.simpletodolist.data.model.TodoItem
import com.example.simpletodolist.data.repository.TodoRepository
import com.example.simpletodolist.notifications.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoListState(
    val todos: List<TodoItem> = emptyList(),
    val unassignedTodos: List<TodoItem> = emptyList(),
    val assignedTodos: List<TodoItem> = emptyList(),
    val uncompletedTodos: List<TodoItem> = emptyList(),
    val completedTodos: List<TodoItem> = emptyList(),
    val isSelectionModeEnabled: Boolean = false,
    val selectedTodoIds: Set<Int> = emptySet()
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
) : ViewModel() {
    private val unassignedItemsFlow = todoRepository.getUnassignedItemsFlow()
    private val assignedItemsFlow = todoRepository.getAssignedItemsFlow()
    private val completedItemsFlow = todoRepository.getCompletedItemsFlow()

    private val _selectionModeState = MutableStateFlow(
        Pair(false, emptySet<Int>())
    )
    private val selectionModeStateFlow: StateFlow<Pair<Boolean, Set<Int>>> = _selectionModeState.asStateFlow()

    val state: StateFlow<TodoListState> = combine(
        unassignedItemsFlow,
        assignedItemsFlow,
        completedItemsFlow,
        selectionModeStateFlow,
    ) { unassigned, assigned, completed, selectionState, ->

        val allTodos = unassigned + assigned + completed

        TodoListState(
            todos = allTodos,
            assignedTodos = assigned,
            unassignedTodos = unassigned,
            uncompletedTodos = assigned + unassigned,
            completedTodos = completed,
            isSelectionModeEnabled = selectionState.first,
            selectedTodoIds = selectionState.second
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodoListState()
        )

    private val _showPermissionDialog = mutableStateOf(false)
    val showPermissionDialog: MutableState<Boolean> = _showPermissionDialog

    fun dismissPermissionDialog() {
        _showPermissionDialog.value = false
    }

    // CRUD

    fun onToggle(todoId: Int) {
        viewModelScope.launch {
            todoRepository.toggleCompletionStatus(todoId)
        }
    }

    fun onAssigned(todoId: Int) {
        viewModelScope.launch {
            todoRepository.toggleAssignedStatus(todoId)
        }
    }

    fun onAdd(title: String, reminderTime: Long?) {
        viewModelScope.launch {
            todoRepository.insertTodo(title, reminderTime)
        }
    }

    fun onRemove(todoId: Int) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todoId)
        }
    }

    fun onEdit(todoId: Int, newTitle: String, reminderTime: Long?) {
        viewModelScope.launch {
            todoRepository.updateTodoContent(
                todoId,
                newTitle,
                reminderTime
            )
        }
    }

    fun checkPermissionsBeforeSettingReminder(
        permissionChecker: () -> Boolean,
        onPermissionsGranted: () -> Boolean
    ): Boolean {
        if (!permissionChecker()) {
            _showPermissionDialog.value = true
            return false
        }

        onPermissionsGranted()
        return true
    }

    // Multi-select mode

    fun onLongPress(todoId: Int) {
        _selectionModeState.value.let { currentState ->
            val (isEnabled, selectedIds) = currentState

            if (!isEnabled) {
                val newSelectedIds = selectedIds + todoId
                _selectionModeState.value = Pair(true, newSelectedIds)
            }
        }
    }

    fun onToggleSelection(todoId: Int) {
        _selectionModeState.value.let { currentState ->
            val (isEnabled, selectedIds) = currentState

            val newSelectedIds = if (selectedIds.contains(todoId)) {
                selectedIds - todoId
            } else {
                selectedIds + todoId
            }

            _selectionModeState.value = Pair(true, newSelectedIds)
        }
    }

    fun onClearSelection() {
        _selectionModeState.value = Pair(false, emptySet())
    }

    fun onSelectAll() {
        _selectionModeState.value.let { currentState ->
            val allTodoIds = state.value.todos.map { it.id }.toSet()
            val (isEnabled, selectedIds) = currentState

            val newSelectedIds = if (allTodoIds.size == selectedIds.size) {
                emptySet()
            } else {
                allTodoIds
            }

            _selectionModeState.value = Pair(true, newSelectedIds)
        }
    }

    fun onRemoveSelected() {
        viewModelScope.launch {
            val selectedIds = state.value.selectedTodoIds

            if (selectedIds.isNotEmpty()) {
                selectedIds.forEach { id ->
                    todoRepository.deleteTodo(id)
                }
                onClearSelection()
            }
        }
    }

    fun onSaveNewOrder(newOrderList: List<TodoItem>) {
        viewModelScope.launch {
            todoRepository.updateOrder(newOrderList)
        }
    }
}