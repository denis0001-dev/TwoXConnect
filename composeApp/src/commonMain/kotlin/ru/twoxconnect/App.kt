package ru.twoxconnect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import twoxconnect.composeapp.generated.resources.Res
import twoxconnect.composeapp.generated.resources.compose_multiplatform
import twoxconnect.composeapp.generated.resources.roboto

val AppTypography
    @Composable
    get() = Typography().let {
        val font = FontFamily(Font(Res.font.roboto))
        it.copy(
            displayLarge = it.displayLarge.copy(fontFamily = font),
            displayMedium = it.displayMedium.copy(fontFamily = font),
            displaySmall = it.displaySmall.copy(fontFamily = font),
            headlineLarge = it.headlineLarge.copy(fontFamily = font),
            headlineMedium = it.headlineMedium.copy(fontFamily = font),
            headlineSmall = it.headlineSmall.copy(fontFamily = font),
            titleLarge = it.titleLarge.copy(fontFamily = font),
            titleMedium = it.titleMedium.copy(fontFamily = font),
            titleSmall = it.titleSmall.copy(fontFamily = font),
            bodyLarge = it.bodyLarge.copy(fontFamily = font),
            bodyMedium = it.bodyMedium.copy(fontFamily = font),
            bodySmall = it.bodySmall.copy(fontFamily = font),
            labelLarge = it.labelLarge.copy(fontFamily = font),
            labelMedium = it.labelMedium.copy(fontFamily = font),
            labelSmall = it.labelSmall.copy(fontFamily = font)
        )
    }

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        typography = AppTypography
    ) {
        Surface(
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxSize()
        ) {
            var showContent by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    val greeting = remember { "test" }
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
            }
        }
    }
}