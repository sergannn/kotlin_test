package com.example.monitorpoc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "object_items")
data class ObjectItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val groupName: String,
    val status: String,
    val lastUpdateTime: Long,
    val isFavorite: Boolean,
    val currentValue: Double?
)

@Entity(tableName = "object_details")
data class ObjectDetailsEntity(
    @PrimaryKey val id: String,
    val name: String,
    val groupName: String,
    val status: String,
    val lastUpdateTime: Long,
    val currentValue: Double?,
    val chartSummary: String,
    val isPartial: Boolean
)

@Entity(tableName = "parameters", primaryKeys = ["objectId", "key"])
data class ParameterEntity(
    val objectId: String,
    val key: String,
    val title: String,
    val value: String?,
    val unit: String,
    val isMissing: Boolean
)

@Entity(tableName = "chart_points", primaryKeys = ["objectId", "timestamp"])
data class ChartPointEntity(
    val objectId: String,
    val timestamp: Long,
    val value: Double
)
