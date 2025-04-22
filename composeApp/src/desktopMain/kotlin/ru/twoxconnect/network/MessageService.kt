@file:Suppress("NOTHING_TO_INLINE")

package ru.twoxconnect.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.ServerSocket
import java.net.Socket

actual object MessageService {
    private val _receivedMessages = MutableStateFlow<List<Message>>(emptyList())
    actual val receivedMessages: StateFlow<List<Message>> = _receivedMessages.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var serverSocket: ServerSocket? = null
    private var isListening = false
    private val MESSAGE_PORT = 12346
    private val json = Json { ignoreUnknownKeys = true }

    actual inline operator fun invoke(): MessageService {
        startListening()
        return this
    }

    actual fun startListening() {
        if (isListening) return
        isListening = true

        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    serverSocket = ServerSocket(MESSAGE_PORT)

                    while (isListening) {
                        val clientSocket = serverSocket?.accept()
                        if (clientSocket != null) {
                            handleClientConnection(clientSocket)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                serverSocket?.close()
                serverSocket = null
            }
        }
    }

    private fun handleClientConnection(clientSocket: Socket) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val inputStream = clientSocket.getInputStream()
                    val buffer = ByteArray(4096)
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead > 0) {
                        val messageJson = String(buffer, 0, bytesRead)
                        val message = json.decodeFromString<Message>(messageJson)
                        receiveMessage(message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                clientSocket.close()
            }
        }
    }

    actual fun sendMessage(device: DiscoveredDevice, message: String) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val socket = Socket(device.ipAddress, device.port)
                    val outputStream = socket.getOutputStream()
                    
                    val messageObj = Message(
                        content = message,
                        sender = "Local Device" // In a real app, this would be the device name
                    )
                    val messageJson = json.encodeToString(messageObj)
                    outputStream.write(messageJson.toByteArray())
                    outputStream.flush()
                    
                    socket.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun receiveMessage(message: Message) {
        _receivedMessages.update { list ->
            list + message
        }
    }

    actual fun stop() {
        isListening = false
        serverSocket?.close()
        serverSocket = null
    }
} 