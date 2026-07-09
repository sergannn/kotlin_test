package com.example.monitorpoc.domain

enum class ObjectStatus {
    NORMAL,
    WARNING,
    CRITICAL
}

data class ObjectItem(
    val id: String,
    val name: String,
    val groupName: String,
    val status: ObjectStatus,
    val lastUpdateTime: Long,
    val isFavorite: Boolean,
    val currentValue: Double?
)

data class ObjectDetails(
    val id: String,
    val name: String,
    val groupName: String,
    val status: ObjectStatus,
    val lastUpdateTime: Long,
    val currentValue: Double?,
    val parameters: List<Parameter>,
    val chartSummary: String,
    val isPartial: Boolean
)

data class Parameter(
    val key: String,
    val title: String,
    val value: String?,
    val unit: String,
    val isMissing: Boolean
)

data class ChartPoint(
    val timestamp: Long,
    val value: Double
)
