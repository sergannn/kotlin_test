package com.example.monitorpoc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.monitorpoc.domain.ObjectStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF00796B),
            error = Color(0xFFC62828)
        ),
        content = content
    )
}

@Composable
fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MessageView(message: String, buttonText: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onClick, modifier = Modifier.padding(top = 12.dp)) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun CacheBanner(fromCache: Boolean, message: String?) {
    if (fromCache || message != null) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(
                text = if (fromCache) "Показаны данные из Room-кэша. ${message.orEmpty()}" else message.orEmpty(),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StatusPill(status: ObjectStatus) {
    val color = when (status) {
        ObjectStatus.NORMAL -> Color(0xFF2E7D32)
        ObjectStatus.WARNING -> Color(0xFFF9A825)
        ObjectStatus.CRITICAL -> Color(0xFFC62828)
    }
    Text(
        text = status.name.lowercase(),
        color = Color.White,
        modifier = Modifier.background(color, MaterialTheme.shapes.small).padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall
    )
}

fun Long.formatTime(): String {
    return SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date(this))
}
