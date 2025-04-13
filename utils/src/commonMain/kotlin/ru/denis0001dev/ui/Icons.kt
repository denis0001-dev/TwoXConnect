@file:Suppress("UnusedReceiverParameter", "unused")

package ru.denis0001dev.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.Language
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.vectorResource
import ru.denis0001dev.utils.Res
import ru.denis0001dev.utils.license
import ru.denis0001dev.utils.siren_filled
import ru.denis0001dev.utils.siren_outlined

inline val Icons.Outlined.Internet get() = Icons.Outlined.Language
inline val Icons.Filled.Internet get() = Icons.Filled.Language

inline val Icons.Outlined.Website get() = Icons.Outlined.Language
inline val Icons.Filled.Website get() = Icons.Filled.Language

inline val Icons.Outlined.License
    @Composable get() = vectorResource(Res.drawable.license)
inline val Icons.Filled.License
    @Composable get() = Icons.Outlined.License

inline val Icons.Outlined.Siren
    @Composable get() = vectorResource(Res.drawable.siren_outlined)
inline val Icons.Filled.Siren
    @Composable get() = vectorResource(Res.drawable.siren_filled)