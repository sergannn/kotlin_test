package com.example.monitorpoc.data.remote

import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

class FakeApiService {
    private var failNextRequest = false
    private val random = Random(42)
    private val baseTime = System.currentTimeMillis()

    fun failNextRequest() {
        failNextRequest = true
    }

    suspend fun login(email: String, password: String): String {
        delay(350)
        if (email.isBlank() || password.length < 4) error("Введите email и пароль от 4 символов")
        return "fake_access_token_${email.hashCode()}"
    }

    suspend fun getObjects(): List<ObjectItemDto> {
        delay(500)
        maybeFail()
        return (1..80).map { index ->
            val missingValue = index % 17 == 0
            ObjectItemDto(
                id = "object-$index",
                name = "Object $index",
                groupName = "Group ${1 + index % 5}",
                status = when {
                    index % 11 == 0 -> "critical"
                    index % 4 == 0 -> "warning"
                    else -> "normal"
                },
                lastUpdateTime = baseTime - index * 12L * 60L * 1000L,
                isFavorite = index % 9 == 0,
                currentValue = if (missingValue) null else 45.0 + index % 20
            )
        }
    }

    suspend fun getDetails(id: String): ObjectDetailsDto {
        delay(450)
        maybeFail()
        val number = id.substringAfter("-").toIntOrNull() ?: 1
        val item = getObjects().first { it.id == id }
        val parameters = (1..7).map { index ->
            val missing = (number + index) % 6 == 0
            ParameterDto(
                key = "p$index",
                title = "Parameter $index",
                value = if (missing) null else "${20 + number + index}",
                unit = if (index % 2 == 0) "ms" else "%",
                isMissing = missing
            )
        }
        return ObjectDetailsDto(
            id = item.id,
            name = item.name,
            groupName = item.groupName,
            status = item.status,
            lastUpdateTime = item.lastUpdateTime,
            currentValue = item.currentValue,
            parameters = parameters,
            chartSummary = if (number % 13 == 0) "No chart data" else "Last 24 hours"
        )
    }

    suspend fun getChartPoints(id: String): List<ChartPointDto> {
        delay(350)
        maybeFail()
        val number = id.substringAfter("-").toIntOrNull() ?: 1
        if (number % 13 == 0) return emptyList()
        val now = System.currentTimeMillis()
        return (0 until 240).map { index ->
            val value = 50 + sin(index / 10.0) * 12 + random.nextDouble(-3.0, 3.0) + number % 8
            ChartPointDto(
                timestamp = now - (239 - index) * 6L * 60L * 1000L,
                value = value
            )
        }
    }

    private fun maybeFail() {
        if (failNextRequest) {
            failNextRequest = false
            error("Fake API error. Cached data can still be shown.")
        }
    }
}
