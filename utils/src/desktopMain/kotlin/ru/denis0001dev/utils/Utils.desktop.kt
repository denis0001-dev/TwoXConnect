package ru.denis0001dev.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual fun Modifier.clearFocusOnKeyboardDismiss() = this

@Composable
actual fun ToggleNavScrimEffect(enabled: Boolean) {}