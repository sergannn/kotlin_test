package com.example.monitorpoc.data.remote

data class ObjectItemDto(
    val id: String,
    val name: String,
    val groupName: String,
    val status: String,
    val lastUpdateTime: Long,
    val isFavorite: Boolean,
    val currentValue: Double?
)

data class ObjectDetailsDto(
    val id: String,
    val name: String,
    val groupName: String,
    val status: String,
    val lastUpdateTime: Long,
    val currentValue: Double?,
    val parameters: List<ParameterDto>,
    val chartSummary: String
)

data class ParameterDto(
    val key: String,
    val title: String,
    val value: String?,
    val unit: String,
    val isMissing: Boolean
)

data class ChartPointDto(
    val timestamp: Long,
    val value: Double
)
