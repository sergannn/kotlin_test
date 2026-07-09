package com.example.monitorpoc.di

import android.content.Context
import androidx.room.Room
import com.example.monitorpoc.data.local.AppDatabase
import com.example.monitorpoc.data.remote.FakeApiService
import com.example.monitorpoc.data.repository.MonitorRepository
import com.example.monitorpoc.security.TokenStorage

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "monitor_poc.db"
    ).build()

    val api = FakeApiService()
    val repository = MonitorRepository(api, database.dao())
    val tokenStorage = TokenStorage(context)
}
