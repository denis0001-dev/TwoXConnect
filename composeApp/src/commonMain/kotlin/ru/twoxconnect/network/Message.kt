package ru.twoxconnect.network

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class Message @OptIn(ExperimentalTime::class) constructor(
    val content: String,
    val sender: String,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)