package ru.twoxconnect.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import ru.denis0001dev.utils.LocalSupportClipboardManager
import ru.denis0001dev.utils.ToggleNavScrimEffect
import ru.denis0001dev.utils.Typography
import ru.denis0001dev.utils.invoke
import ru.denis0001dev.utils.supportClipboardManagerImpl
import ru.twoxconnect.Res
import ru.twoxconnect.roboto

interface WindowInsetsScope {
    val systemBarInsets: WindowInsets
    val isWindowInsetsConsumed: Boolean

    val topInset: Int
    val bottomInset: Int
    val leftInset: Int
    val rightInset: Int
}

@Composable
expect inline fun ProvideContextMenuRepresentation(darkTheme: Boolean, crossinline content: @Composable () -> Unit)

expect fun colorScheme(darkTheme: Boolean): ColorScheme

val LocalWindowInsetsScope: ProvidableCompositionLocal<WindowInsetsScope> = compositionLocalOf { error("") }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    consumeWindowInsets: Boolean = false,
    removeScrim: Boolean = true,
    content: @Composable WindowInsetsScope.() -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme(darkTheme),
        typography = Typography(Res.font.roboto)
    ) {
        ToggleNavScrimEffect(!removeScrim)

        val insets = WindowInsets(
            WindowInsets.systemBars.getLeft(
                LocalDensity(),
                LocalLayoutDirection()
            ),
            WindowInsets.systemBars.getTop(LocalDensity()),
            WindowInsets.systemBars.getRight(
                LocalDensity(),
                LocalLayoutDirection()
            ),
            WindowInsets.systemBars.getBottom(LocalDensity())
        )

        ProvideContextMenuRepresentation(darkTheme) {
            CompositionLocalProvider(
                LocalSupportClipboardManager provides supportClipboardManagerImpl
            ) {
                Surface(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.let {
                        val mod = it.fillMaxSize()
                        if (consumeWindowInsets) {
                            mod.consumeWindowInsets(
                                WindowInsets.navigationBars.only(WindowInsetsSides.Vertical)
                            )
                        }
                        mod
                    }
                ) {
                    val topInset = insets.getTop(LocalDensity())
                    val bottomInset = insets.getBottom(LocalDensity())
                    val leftInset = insets.getLeft(LocalDensity(), LocalLayoutDirection())
                    val rightInset = insets.getRight(LocalDensity(), LocalLayoutDirection())

                    val scope = object : WindowInsetsScope {
                        override val systemBarInsets get() = insets
                        override val isWindowInsetsConsumed get() = consumeWindowInsets
                        override val topInset get() = topInset
                        override val bottomInset get() = bottomInset
                        override val leftInset get() = leftInset
                        override val rightInset get() = rightInset
                    }

                    CompositionLocalProvider(LocalWindowInsetsScope provides scope) {
                        content(scope)
                    }
                }
            }
        }
    }
}