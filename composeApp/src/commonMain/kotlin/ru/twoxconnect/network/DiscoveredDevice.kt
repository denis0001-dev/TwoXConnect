package ru.twoxconnect.network

data class DiscoveredDevice(
    val name: String,
    val ipAddress: String,
    val port: Int
)