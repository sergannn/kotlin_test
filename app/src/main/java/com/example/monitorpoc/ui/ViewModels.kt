package com.example.monitorpoc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.monitorpoc.data.repository.MonitorRepository
import com.example.monitorpoc.domain.ChartPoint
import com.example.monitorpoc.domain.ObjectDetails
import com.example.monitorpoc.domain.ObjectItem
import com.example.monitorpoc.domain.ObjectStatus
import com.example.monitorpoc.security.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val tokenStorage: TokenStorage,
    private val repository: MonitorRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState(isLoggedIn = tokenStorage.hasToken()))
    val state: StateFlow<LoginState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val token = repository.login(email, password)
                tokenStorage.saveToken(token)
                _state.value = LoginState(isLoggedIn = true)
            } catch (error: Throwable) {
                _state.update { it.copy(isLoading = false, error = error.message ?: "Login error") }
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            tokenStorage.clear()
            repository.clearCache()
            _state.value = LoginState(isLoggedIn = false)
            onDone()
        }
    }
}

data class LoginState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ListScreenState(
    val itemsState: UiState<List<ObjectItem>> = UiState.Loading,
    val query: String = "",
    val selectedStatus: ObjectStatus? = null,
    val newestFirst: Boolean = true
) {
    val visibleItems: List<ObjectItem>
        get() {
            val source = (itemsState as? UiState.Content)?.data.orEmpty()
            return source
                .filter { it.name.contains(query, ignoreCase = true) }
                .filter { selectedStatus == null || it.status == selectedStatus }
                .let { items ->
                    if (newestFirst) items.sortedByDescending { it.lastUpdateTime }
                    else items.sortedBy { it.lastUpdateTime }
                }
        }
}

class ObjectListViewModel(private val repository: MonitorRepository) : ViewModel() {
    private val _state = MutableStateFlow(ListScreenState())
    val state: StateFlow<ListScreenState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(itemsState = UiState.Loading) }
            try {
                val result = repository.loadObjects()
                _state.update {
                    it.copy(
                        itemsState = if (result.data.isEmpty()) UiState.Empty else UiState.Content(
                            data = result.data,
                            fromCache = result.fromCache,
                            message = result.message
                        )
                    )
                }
            } catch (error: Throwable) {
                _state.update { it.copy(itemsState = UiState.Error(error.message ?: "Load error")) }
            }
        }
    }

    fun simulateNextError() {
        repository.failNextRequest()
        refresh()
    }

    fun setQuery(value: String) {
        _state.update { it.copy(query = value) }
    }

    fun setStatus(value: ObjectStatus?) {
        _state.update { it.copy(selectedStatus = value) }
    }

    fun toggleSort() {
        _state.update { it.copy(newestFirst = !it.newestFirst) }
    }
}

class ObjectDetailsViewModel(
    private val repository: MonitorRepository,
    private val id: String
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<ObjectDetails>>(UiState.Loading)
    val state: StateFlow<UiState<ObjectDetails>> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val result = repository.loadDetails(id)
                _state.value = UiState.Content(result.data, result.fromCache, result.message)
            } catch (error: Throwable) {
                _state.value = UiState.Error(error.message ?: "Details error")
            }
        }
    }
}

class ChartViewModel(
    private val repository: MonitorRepository,
    private val id: String
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<ChartPoint>>>(UiState.Loading)
    val state: StateFlow<UiState<List<ChartPoint>>> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val result = repository.loadChartPoints(id)
                _state.value = if (result.data.isEmpty()) UiState.Empty else UiState.Content(result.data, result.fromCache, result.message)
            } catch (error: Throwable) {
                _state.value = UiState.Error(error.message ?: "Chart error")
            }
        }
    }
}

class SimpleFactory<T : ViewModel>(private val create: () -> T) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
}
