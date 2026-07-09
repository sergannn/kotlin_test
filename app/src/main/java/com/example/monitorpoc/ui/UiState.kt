package com.example.monitorpoc.ui

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Content<T>(
        val data: T,
        val fromCache: Boolean = false,
        val message: String? = null
    ) : UiState<T>
}
