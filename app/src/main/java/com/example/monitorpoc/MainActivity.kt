package com.example.monitorpoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitorpoc.di.AppContainer
import com.example.monitorpoc.ui.ChartViewModel
import com.example.monitorpoc.ui.LoginViewModel
import com.example.monitorpoc.ui.ObjectDetailsViewModel
import com.example.monitorpoc.ui.ObjectListViewModel
import com.example.monitorpoc.ui.SimpleFactory
import com.example.monitorpoc.ui.screens.AppTheme
import com.example.monitorpoc.ui.screens.ChartScreen
import com.example.monitorpoc.ui.screens.DetailsScreen
import com.example.monitorpoc.ui.screens.ListScreen
import com.example.monitorpoc.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val container = remember { AppContainer(applicationContext) }
            AppTheme {
                MonitorRoot(container)
            }
        }
    }
}

private sealed interface Screen {
    data object Login : Screen
    data object List : Screen
    data class Details(val id: String) : Screen
    data class Chart(val id: String) : Screen
}

@Composable
private fun MonitorRoot(container: AppContainer) {
    val loginViewModel: LoginViewModel = viewModel(
        factory = SimpleFactory { LoginViewModel(container.tokenStorage, container.repository) }
    )
    val loginState by loginViewModel.state.collectAsStateWithLifecycle()
    var screen by remember(loginState.isLoggedIn) {
        mutableStateOf<Screen>(if (loginState.isLoggedIn) Screen.List else Screen.Login)
    }

    when (val current = screen) {
        Screen.Login -> LoginScreen(
            state = loginState,
            onLogin = loginViewModel::login
        )

        Screen.List -> {
            val listViewModel: ObjectListViewModel = viewModel(
                factory = SimpleFactory { ObjectListViewModel(container.repository) }
            )
            val state by listViewModel.state.collectAsStateWithLifecycle()
            ListScreen(
                state = state,
                onSearch = listViewModel::setQuery,
                onStatus = listViewModel::setStatus,
                onToggleSort = listViewModel::toggleSort,
                onRefresh = listViewModel::refresh,
                onSimulateError = listViewModel::simulateNextError,
                onOpenDetails = { screen = Screen.Details(it) },
                onLogout = { loginViewModel.logout { screen = Screen.Login } }
            )
        }

        is Screen.Details -> {
            val detailsViewModel: ObjectDetailsViewModel = viewModel(
                key = "details-${current.id}",
                factory = SimpleFactory { ObjectDetailsViewModel(container.repository, current.id) }
            )
            val state by detailsViewModel.state.collectAsStateWithLifecycle()
            DetailsScreen(
                state = state,
                onBack = { screen = Screen.List },
                onRefresh = detailsViewModel::refresh,
                onOpenChart = { screen = Screen.Chart(current.id) }
            )
        }

        is Screen.Chart -> {
            val chartViewModel: ChartViewModel = viewModel(
                key = "chart-${current.id}",
                factory = SimpleFactory { ChartViewModel(container.repository, current.id) }
            )
            val state by chartViewModel.state.collectAsStateWithLifecycle()
            ChartScreen(
                state = state,
                onBack = { screen = Screen.Details(current.id) },
                onRefresh = chartViewModel::refresh
            )
        }
    }
}
