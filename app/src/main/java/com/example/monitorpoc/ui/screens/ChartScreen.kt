package com.example.monitorpoc.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.monitorpoc.domain.ChartPoint
import com.example.monitorpoc.ui.UiState

@Composable
fun ChartScreen(
    state: UiState<List<ChartPoint>>,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("Back") }
            TextButton(onClick = onRefresh) { Text("Refresh") }
        }
        Text("Canvas chart", style = MaterialTheme.typography.titleLarge)
        when (state) {
            UiState.Loading -> LoadingView()
            UiState.Empty -> MessageView("Для объекта нет точек графика", "Обновить", onRefresh)
            is UiState.Error -> MessageView(state.message, "Повторить", onRefresh)
            is UiState.Content -> {
                CacheBanner(state.fromCache, state.message)
                ChartCard(state.data)
            }
        }
    }
}

@Composable
private fun ChartCard(points: List<ChartPoint>) {
    val minValue = points.minOf { it.value }
    val maxValue = points.maxOf { it.value }
    Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Points: ${points.size}")
                Text("${minValue.toInt()}..${maxValue.toInt()}")
            }
            LineChart(
                points = points,
                modifier = Modifier.fillMaxWidth().height(260.dp).padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun LineChart(points: List<ChartPoint>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val left = 44f
        val right = size.width - 12f
        val top = 12f
        val bottom = size.height - 32f
        val chartWidth = right - left
        val chartHeight = bottom - top
        val minValue = points.minOf { it.value }
        val maxValue = points.maxOf { it.value }
        val range = (maxValue - minValue).takeIf { it > 0.0 } ?: 1.0

        drawLine(Color.LightGray, Offset(left, top), Offset(left, bottom), strokeWidth = 2f)
        drawLine(Color.LightGray, Offset(left, bottom), Offset(right, bottom), strokeWidth = 2f)
        repeat(4) { index ->
            val y = top + chartHeight * index / 3f
            drawLine(Color(0xFFE0E0E0), Offset(left, y), Offset(right, y), strokeWidth = 1f)
        }

        val path = Path()
        points.forEachIndexed { index, point ->
            val x = left + chartWidth * index / (points.lastIndex.coerceAtLeast(1))
            val normalized = ((point.value - minValue) / range).toFloat()
            val y = bottom - chartHeight * normalized
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = Color(0xFF1976D2),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}
