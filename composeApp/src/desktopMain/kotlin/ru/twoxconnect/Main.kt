package ru.twoxconnect

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import ru.twoxconnect.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TwoXConnect",
        icon = painterResource(Res.drawable.icon),
        state = rememberWindowState(width = 600.dp, height = 750.dp)
    ) {
        App()
    }
}