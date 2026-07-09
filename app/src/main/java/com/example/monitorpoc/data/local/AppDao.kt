package com.example.monitorpoc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDao {
    @Query("SELECT * FROM object_items ORDER BY lastUpdateTime DESC")
    suspend fun getObjects(): List<ObjectItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveObjects(items: List<ObjectItemEntity>)

    @Query("SELECT * FROM object_details WHERE id = :id")
    suspend fun getDetails(id: String): ObjectDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDetails(details: ObjectDetailsEntity)

    @Query("SELECT * FROM parameters WHERE objectId = :id")
    suspend fun getParameters(id: String): List<ParameterEntity>

    @Query("DELETE FROM parameters WHERE objectId = :id")
    suspend fun deleteParameters(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveParameters(parameters: List<ParameterEntity>)

    @Query("SELECT * FROM chart_points WHERE objectId = :id ORDER BY timestamp ASC")
    suspend fun getChartPoints(id: String): List<ChartPointEntity>

    @Query("DELETE FROM chart_points WHERE objectId = :id")
    suspend fun deleteChartPoints(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChartPoints(points: List<ChartPointEntity>)

    @Query("DELETE FROM object_items")
    suspend fun clearObjects()

    @Query("DELETE FROM object_details")
    suspend fun clearDetails()

    @Query("DELETE FROM parameters")
    suspend fun clearParameters()

    @Query("DELETE FROM chart_points")
    suspend fun clearChartPoints()
}
