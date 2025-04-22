package ru.twoxconnect.network

import kotlinx.coroutines.flow.StateFlow

expect object NetworkDiscovery {
    fun startDiscovery()
    fun stopDiscovery()
    val discoveredDevices: StateFlow<List<DiscoveredDevice>>
}
