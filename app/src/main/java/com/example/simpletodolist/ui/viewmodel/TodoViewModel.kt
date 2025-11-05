package com.example.simpletodolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simpletodolist.data.dao.TodoDao // ⬅️ Теперь используем DAO
import com.example.simpletodolist.data.entity.TodoItem
import com.example.simpletodolist.data.service.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodoListState(
    val todos: List<TodoItem> = emptyList(),
    val uncompletedTodos: List<TodoItem> = emptyList(),
    val completedTodos: List<TodoItem> = emptyList(),
    val isSelectionModeEnabled: Boolean = false,
    val selectedTodoIds: Set<Int> = emptySet()
)

class TodoViewModel(
    private val todoDao: TodoDao,
    private val notificationService: NotificationService
) : ViewModel() {
    private val _selectionModeState = MutableStateFlow(
        Pair(false, emptySet<Int>())
    )
    private val selectionModeStateFlow: StateFlow<Pair<Boolean, Set<Int>>> = _selectionModeState.asStateFlow()

    private val uncompletedItemsFlow = todoDao.getUncompletedItems()
    private val completedItemsFlow = todoDao.getCompletedItems()
    val state: StateFlow<TodoListState> = combine(
        uncompletedItemsFlow,
        completedItemsFlow,
        selectionModeStateFlow
    ) { uncompleted, completed, selectionState ->

        val allTodos = uncompleted + completed

        TodoListState(
            todos = allTodos,
            uncompletedTodos = uncompleted,
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

    // CRUD
    fun onToggle(todoId: Int) {
        viewModelScope.launch {
            val item = todoDao.getItem(todoId)
            if (item != null) {
                val newStatus = !item.isCompleted
                var newReminderTime: Long? = item.reminderTime
                var newCompletionDate: Long? = null

                if (newStatus) {
                    newCompletionDate = System.currentTimeMillis()
                    if (item.reminderTime != null) {
                        notificationService.cancelReminder(todoId)
                    }
                    newReminderTime = null
                }

                todoDao.update(
                    item.copy(
                        isCompleted = newStatus,
                        completionDate = newCompletionDate,
                        reminderTime = newReminderTime
                    )
                )
            }
        }
    }

    fun onAssigned(todoId: Int) {
        viewModelScope.launch {
            val item = todoDao.getItem(todoId)
            if (item != null) {
                val newStatus = !item.isAssigned

                todoDao.update(
                    item.copy(
                        isAssigned = newStatus,
                    )
                )
            }
        }
    }

    fun onAdd(title: String, reminderTime: Long?) {
        viewModelScope.launch {
            val newTodo = TodoItem(
                title = title,
                orderIndex = state.value.uncompletedTodos.size,
                reminderTime = reminderTime
            )
            val newRowId = todoDao.insert(newTodo)
            val newId = newRowId.toInt()
            if (reminderTime != null) {
                notificationService.scheduleReminder(
                    id = newId,
                    title = title,
                    timeMillis = reminderTime
                )
            }
        }
    }

    fun onRemove(todoId: Int) {
        viewModelScope.launch {
            val item = todoDao.getItem(todoId)
            if (item != null) {
                todoDao.delete(todoId)
            }
        }
    }

    fun onEdit(todoId: Int, newTitle: String, reminderTime: Long?) {
        viewModelScope.launch {
            val currentTodo = todoDao.getItem(todoId)
            if (currentTodo != null) {
                notificationService.cancelReminder(todoId)

                val updatedTodo = currentTodo.copy(
                    title = newTitle,
                    reminderTime = reminderTime
                )
                todoDao.update(updatedTodo)

                if (reminderTime != null) {
                    notificationService.scheduleReminder(
                        id = todoId,
                        title = newTitle,
                        timeMillis = reminderTime
                    )
                }
            }
        }
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
                    todoDao.delete(id)
                }
                onClearSelection()
            }
        }
    }
    fun onSaveNewOrder(newOrderList: List<TodoItem>) {
        viewModelScope.launch {
            val itemsToUpdate = newOrderList.mapIndexed { index, item ->
                item.copy(orderIndex = index)
            }

            if (itemsToUpdate.isNotEmpty()) {
                todoDao.update(itemsToUpdate)
            }
        }
    }
}

class TodoViewModelFactory(
    private val todoDao: TodoDao,
    private val notificationService: NotificationService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(
                todoDao = todoDao,
                notificationService = notificationService
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}