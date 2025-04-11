@file:Suppress("NOTHING_TO_INLINE")

package ru.denis0001dev

import androidx.compose.runtime.Composable

@Composable
actual inline fun currentWindowSize() = androidx.compose.material3.adaptive.currentWindowSize()