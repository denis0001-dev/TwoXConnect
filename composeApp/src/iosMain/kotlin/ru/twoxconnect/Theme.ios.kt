package ru.twoxconnect

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

actual fun colorScheme(darkTheme: Boolean) = if (darkTheme) darkColorScheme() else lightColorScheme()