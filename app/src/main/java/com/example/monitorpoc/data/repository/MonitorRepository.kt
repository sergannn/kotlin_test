package com.example.monitorpoc.data.repository

import com.example.monitorpoc.data.local.AppDao
import com.example.monitorpoc.data.remote.FakeApiService
import com.example.monitorpoc.domain.ChartPoint
import com.example.monitorpoc.domain.ObjectDetails
import com.example.monitorpoc.domain.ObjectItem

data class RepositoryResult<T>(
    val data: T,
    val fromCache: Boolean,
    val message: String? = null
)

class MonitorRepository(
    private val api: FakeApiService,
    private val dao: AppDao
) {
    suspend fun login(email: String, password: String): String {
        return api.login(email, password)
    }

    fun failNextRequest() {
        api.failNextRequest()
    }

    suspend fun loadObjects(): RepositoryResult<List<ObjectItem>> {
        return try {
            val remote = api.getObjects()
            dao.saveObjects(remote.map { it.toEntity() })
            RepositoryResult(remote.map { it.toEntity().toDomain() }, fromCache = false)
        } catch (error: Throwable) {
            val cached = dao.getObjects().map { it.toDomain() }
            if (cached.isNotEmpty()) {
                RepositoryResult(cached, fromCache = true, message = error.message)
            } else {
                throw error
            }
        }
    }

    suspend fun loadDetails(id: String): RepositoryResult<ObjectDetails> {
        return try {
            val remote = api.getDetails(id)
            dao.saveDetails(remote.toEntity())
            dao.deleteParameters(id)
            dao.saveParameters(remote.parametersToEntities())
            RepositoryResult(remote.toEntity().toDomain(remote.parametersToEntities().map { it.toDomain() }), false)
        } catch (error: Throwable) {
            val details = dao.getDetails(id)
            if (details != null) {
                val parameters = dao.getParameters(id).map { it.toDomain() }
                RepositoryResult(details.toDomain(parameters), fromCache = true, message = error.message)
            } else {
                throw error
            }
        }
    }

    suspend fun loadChartPoints(id: String): RepositoryResult<List<ChartPoint>> {
        return try {
            val remote = api.getChartPoints(id)
            dao.deleteChartPoints(id)
            dao.saveChartPoints(remote.map { it.toEntity(id) })
            RepositoryResult(remote.map { ChartPoint(it.timestamp, it.value) }, false)
        } catch (error: Throwable) {
            val cached = dao.getChartPoints(id).map { it.toDomain() }
            if (cached.isNotEmpty()) {
                RepositoryResult(cached, fromCache = true, message = error.message)
            } else {
                throw error
            }
        }
    }

    suspend fun clearCache() {
        dao.clearChartPoints()
        dao.clearParameters()
        dao.clearDetails()
        dao.clearObjects()
    }
}
