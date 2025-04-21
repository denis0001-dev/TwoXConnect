package ru.twoxconnect.network

import kotlinx.coroutines.flow.StateFlow


expect object MessageService {
    inline operator fun invoke(): MessageService
    fun startListening()
    fun sendMessage(device: DiscoveredDevice, message: String)
    val receivedMessages: StateFlow<List<Message>>
    fun stop()
}
