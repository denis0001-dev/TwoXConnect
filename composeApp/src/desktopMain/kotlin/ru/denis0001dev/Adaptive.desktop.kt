@file:Suppress("NOTHING_TO_INLINE")

package ru.denis0001dev

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual inline fun currentWindowSize() = LocalWindowInfo.current.containerSize