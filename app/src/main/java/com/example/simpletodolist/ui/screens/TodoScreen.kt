package com.example.simpletodolist.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpletodolist.ui.components.TodoListContent
import com.example.simpletodolist.ui.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    factory: ViewModelProvider.Factory
) {
    val viewModel: TodoViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsState()

    TodoListContent(
        state = state,
        onToggle = viewModel::onToggle,
        onAdd = viewModel::onAdd,
        onRemove = viewModel::onRemove,
        onEdit = viewModel::onEdit,
        onLongPress = viewModel::onLongPress,
        onToggleSelection = viewModel::onToggleSelection,
        onClearSelection = viewModel::onClearSelection,
        onSelectAll = viewModel::onSelectAll,
        onRemoveSelected = viewModel::onRemoveSelected,
        onSaveNewOrder = viewModel::onSaveNewOrder,
        onAssign = viewModel::onAssigned
    )
}