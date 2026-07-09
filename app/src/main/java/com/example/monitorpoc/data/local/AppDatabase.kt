package com.example.monitorpoc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ObjectItemEntity::class,
        ObjectDetailsEntity::class,
        ParameterEntity::class,
        ChartPointEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
