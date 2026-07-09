package com.example.monitorpoc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.monitorpoc.domain.ObjectItem
import com.example.monitorpoc.domain.ObjectStatus
import com.example.monitorpoc.ui.ListScreenState
import com.example.monitorpoc.ui.UiState

@Composable
fun ListScreen(
    state: ListScreenState,
    onSearch: (String) -> Unit,
    onStatus: (ObjectStatus?) -> Unit,
    onToggleSort: () -> Unit,
    onRefresh: () -> Unit,
    onSimulateError: () -> Unit,
    onOpenDetails: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Objects", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onLogout) { Text("Logout") }
        }
        OutlinedTextField(
            value = state.query,
            onValueChange = onSearch,
            label = { Text("Search by name") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(selected = state.selectedStatus == null, onClick = { onStatus(null) }, label = { Text("all") })
            ObjectStatus.entries.forEach { status ->
                FilterChip(
                    selected = state.selectedStatus == status,
                    onClick = { onStatus(status) },
                    label = { Text(status.name.lowercase()) }
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onToggleSort) {
                Text(if (state.newestFirst) "Newest first" else "Oldest first")
            }
            Button(onClick = onRefresh) { Text("Refresh") }
            TextButton(onClick = onSimulateError) { Text("API error") }
        }

        when (val itemsState = state.itemsState) {
            UiState.Loading -> LoadingView()
            UiState.Empty -> MessageView("Список пуст", "Обновить", onRefresh)
            is UiState.Error -> MessageView(itemsState.message, "Повторить", onRefresh)
            is UiState.Content -> {
                CacheBanner(itemsState.fromCache, itemsState.message)
                if (state.visibleItems.isEmpty()) {
                    MessageView("Ничего не найдено по фильтрам", "Сбросить поиск") {
                        onSearch("")
                        onStatus(null)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.visibleItems, key = { it.id }) { item ->
                            ObjectRow(item = item, onClick = { onOpenDetails(item.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ObjectRow(item: ObjectItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                StatusPill(item.status)
            }
            Text(item.groupName, style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Updated: ${item.lastUpdateTime.formatTime()}")
                Text("Value: ${item.currentValue?.toString() ?: "missing"}")
            }
            if (item.isFavorite) {
                Spacer(Modifier.padding(top = 2.dp))
                Text("Favorite", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
