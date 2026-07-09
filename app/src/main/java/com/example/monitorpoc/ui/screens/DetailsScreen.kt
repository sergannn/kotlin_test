package com.example.monitorpoc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.monitorpoc.domain.ObjectDetails
import com.example.monitorpoc.domain.Parameter
import com.example.monitorpoc.ui.UiState

@Composable
fun DetailsScreen(
    state: UiState<ObjectDetails>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onOpenChart: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("Back") }
            TextButton(onClick = onRefresh) { Text("Refresh") }
        }
        when (state) {
            UiState.Loading -> LoadingView()
            UiState.Empty -> MessageView("Нет данных карточки", "Назад", onBack)
            is UiState.Error -> MessageView(state.message, "Повторить", onRefresh)
            is UiState.Content -> DetailsContent(state.data, state.fromCache, state.message, onOpenChart)
        }
    }
}

@Composable
private fun DetailsContent(
    details: ObjectDetails,
    fromCache: Boolean,
    message: String?,
    onOpenChart: () -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            CacheBanner(fromCache, message)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(details.name, style = MaterialTheme.typography.titleLarge)
                StatusPill(details.status)
            }
            Text("${details.groupName} · updated ${details.lastUpdateTime.formatTime()}")
            Text("Current value: ${details.currentValue?.toString() ?: "missing"}")
            if (details.isPartial) {
                Text("Partial data: часть параметров отсутствует", color = Color(0xFFC62828))
            }
            Button(onClick = onOpenChart, modifier = Modifier.padding(top = 8.dp)) {
                Text("Open chart")
            }
        }
        items(details.parameters, key = { it.key }) { parameter ->
            ParameterRow(parameter)
        }
    }
}

@Composable
private fun ParameterRow(parameter: Parameter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (parameter.isMissing) Color(0xFFFFEBEE) else Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(parameter.title, style = MaterialTheme.typography.titleSmall)
                Text(parameter.key, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = if (parameter.isMissing) "missing" else "${parameter.value} ${parameter.unit}",
                color = if (parameter.isMissing) Color(0xFFC62828) else Color.Unspecified
            )
        }
    }
}
