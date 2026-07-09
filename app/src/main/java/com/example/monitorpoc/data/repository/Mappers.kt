package com.example.monitorpoc.data.repository

import com.example.monitorpoc.data.local.ChartPointEntity
import com.example.monitorpoc.data.local.ObjectDetailsEntity
import com.example.monitorpoc.data.local.ObjectItemEntity
import com.example.monitorpoc.data.local.ParameterEntity
import com.example.monitorpoc.data.remote.ChartPointDto
import com.example.monitorpoc.data.remote.ObjectDetailsDto
import com.example.monitorpoc.data.remote.ObjectItemDto
import com.example.monitorpoc.domain.ChartPoint
import com.example.monitorpoc.domain.ObjectDetails
import com.example.monitorpoc.domain.ObjectItem
import com.example.monitorpoc.domain.ObjectStatus
import com.example.monitorpoc.domain.Parameter

fun String.toStatus(): ObjectStatus = when (lowercase()) {
    "warning" -> ObjectStatus.WARNING
    "critical" -> ObjectStatus.CRITICAL
    else -> ObjectStatus.NORMAL
}

fun ObjectItemDto.toEntity() = ObjectItemEntity(
    id = id,
    name = name,
    groupName = groupName,
    status = status,
    lastUpdateTime = lastUpdateTime,
    isFavorite = isFavorite,
    currentValue = currentValue
)

fun ObjectItemEntity.toDomain() = ObjectItem(
    id = id,
    name = name,
    groupName = groupName,
    status = status.toStatus(),
    lastUpdateTime = lastUpdateTime,
    isFavorite = isFavorite,
    currentValue = currentValue
)

fun ObjectDetailsDto.toEntity() = ObjectDetailsEntity(
    id = id,
    name = name,
    groupName = groupName,
    status = status,
    lastUpdateTime = lastUpdateTime,
    currentValue = currentValue,
    chartSummary = chartSummary,
    isPartial = currentValue == null || parameters.any { it.isMissing }
)

fun ObjectDetailsEntity.toDomain(parameters: List<Parameter>) = ObjectDetails(
    id = id,
    name = name,
    groupName = groupName,
    status = status.toStatus(),
    lastUpdateTime = lastUpdateTime,
    currentValue = currentValue,
    parameters = parameters,
    chartSummary = chartSummary,
    isPartial = isPartial
)

fun ObjectDetailsDto.parametersToEntities() = parameters.map {
    ParameterEntity(
        objectId = id,
        key = it.key,
        title = it.title,
        value = it.value,
        unit = it.unit,
        isMissing = it.isMissing
    )
}

fun ParameterEntity.toDomain() = Parameter(
    key = key,
    title = title,
    value = value,
    unit = unit,
    isMissing = isMissing
)

fun ChartPointDto.toEntity(objectId: String) = ChartPointEntity(
    objectId = objectId,
    timestamp = timestamp,
    value = value
)

fun ChartPointEntity.toDomain() = ChartPoint(timestamp = timestamp, value = value)
