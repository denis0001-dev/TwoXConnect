package ru.twoxconnect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import ru.denis0001dev.invoke
import ru.denis0001dev.plus
import ru.denis0001dev.resetFocus

@Composable
inline fun TabBase(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController()
    val focusManager = LocalFocusManager()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null    // this gets rid of the ripple effect
            ) {
                resetFocus(keyboardController, focusManager)
            } + modifier,
    ) {
        content()
    }
}