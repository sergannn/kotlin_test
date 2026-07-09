package com.example.monitorpoc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.monitorpoc.ui.LoginState

@Composable
fun LoginScreen(
    state: LoginState,
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("test@example.com") }
    var password by remember { mutableStateOf("1234") }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(48.dp))
        Text("Monitor PoC")
        Text("Fake login, token сохраняется через EncryptedSharedPreferences")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (state.error != null) {
            Text(state.error, modifier = Modifier.padding(top = 8.dp))
        }
        Button(
            onClick = { onLogin(email, password) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(if (state.isLoading) "Вход..." else "Войти")
        }
        TextButton(onClick = { onLogin("", "") }) {
            Text("Показать ошибку валидации")
        }
    }
}
