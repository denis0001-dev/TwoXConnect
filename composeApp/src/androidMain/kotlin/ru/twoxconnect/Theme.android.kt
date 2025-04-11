package ru.twoxconnect

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

actual fun colorScheme(darkTheme: Boolean): ColorScheme {
    return if (Build.VERSION.SDK_INT >= 31) {
        if (darkTheme) {
            dynamicDarkColorScheme(TwoXConnectApp.context)
        } else {
            dynamicLightColorScheme(TwoXConnectApp.context)
        }
    } else {
        if (darkTheme) {
            lightColorScheme()
        } else {
            darkColorScheme()
        }
    }
}

@Composable
actual inline fun ProvideContextMenuRepresentation(darkTheme: Boolean, content: @Composable () -> Unit) {
    content()
}