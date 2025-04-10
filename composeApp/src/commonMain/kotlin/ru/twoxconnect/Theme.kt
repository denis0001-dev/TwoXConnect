package ru.twoxconnect

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.denis0001dev.Typography
import twoxconnect.composeapp.generated.resources.Res
import twoxconnect.composeapp.generated.resources.roboto

expect fun colorScheme(darkTheme: Boolean): ColorScheme

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme(darkTheme),
        typography = Typography(Res.font.roboto)
    ) {
        content()
    }
}