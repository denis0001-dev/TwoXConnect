package ru.twoxconnect.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

actual fun colorScheme(darkTheme: Boolean) = if (darkTheme) darkColorScheme() else lightColorScheme()

@Composable
actual inline fun ProvideContextMenuRepresentation(darkTheme: Boolean, content: @Composable () -> Unit) {
    content()
}

@Composable
actual fun fixStatusBar(darkTheme: Boolean) {}