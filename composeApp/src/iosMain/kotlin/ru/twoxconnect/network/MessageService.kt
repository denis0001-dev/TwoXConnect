@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalNativeApi::class)

package ru.twoxconnect.network

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import platform.posix.AF_INET
import platform.posix.INADDR_ANY
import platform.posix.SOCK_STREAM
import platform.posix.accept
import platform.posix.bind
import platform.posix.close
import platform.posix.connect
import platform.posix.listen
import platform.posix.recv
import platform.posix.send
import platform.posix.sockaddr_in
import platform.posix.socket
import ru.denis0001dev.utils.htons
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class)
private fun createSockaddrIn(port: UShort, address: UInt = INADDR_ANY): CPointer<sockaddr_in> {
    return nativeHeap.alloc<sockaddr_in>().apply {
        sin_family = AF_INET.toUByte()
        sin_port = htons(port)
        sin_addr.s_addr = address
    }.ptr
}

@OptIn(ExperimentalForeignApi::class)
private fun inetAddr(ip: String): UInt {
    val parts = ip.split(".")
    if (parts.size != 4) return INADDR_ANY
    
    return ((parts[0].toUInt() shl 24) or
            (parts[1].toUInt() shl 16) or
            (parts[2].toUInt() shl 8) or
            parts[3].toUInt())
}

actual object MessageService {
    private val _receivedMessages = MutableStateFlow<List<Message>>(emptyList())
    actual val receivedMessages: StateFlow<List<Message>> = _receivedMessages.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var serverSocket: Int = -1
    private var isListening = false
    private val MESSAGE_PORT = 12346
    private val json = Json { ignoreUnknownKeys = true }

    actual inline operator fun invoke(): MessageService {
        startListening()
        return this
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun startListening() {
        if (isListening) return
        isListening = true

        scope.launch {
            try {
                serverSocket = socket(AF_INET, SOCK_STREAM, 0)
                if (serverSocket == -1) {
                    println("Failed to create socket")
                    return@launch
                }

                val serverAddr = createSockaddrIn(MESSAGE_PORT.toUShort())
                if (bind(serverSocket, serverAddr.reinterpret(), sizeOf<sockaddr_in>().toUInt()) == -1) {
                    println("Failed to bind socket")
                    close(serverSocket)
                    return@launch
                }

                if (listen(serverSocket, 5) == -1) {
                    println("Failed to listen on socket")
                    close(serverSocket)
                    return@launch
                }

                while (isListening) {
                    val clientAddr = nativeHeap.alloc<sockaddr_in>()
                    val addrLen = nativeHeap.alloc<UIntVar>().apply { value = sizeOf<sockaddr_in>().toUInt() }
                    val clientSocket = accept(serverSocket, clientAddr.ptr.reinterpret(), addrLen.ptr)
                    
                    if (clientSocket != -1) {
                        handleClientConnection(clientSocket)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (serverSocket != -1) {
                    close(serverSocket)
                    serverSocket = -1
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun handleClientConnection(clientSocket: Int) {
        scope.launch {
            try {
                val buffer = ByteArray(4096)
                val bytesRead = recv(clientSocket, buffer.refTo(0), buffer.size.toULong(), 0)
                
                if (bytesRead > 0) {
                    val messageJson = buffer.decodeToString(0, bytesRead.toInt())
                    val message = json.decodeFromString<Message>(messageJson)
                    receiveMessage(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                close(clientSocket)
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun sendMessage(device: DiscoveredDevice, message: String) {
        scope.launch {
            try {
                val clientSocket = socket(AF_INET, SOCK_STREAM, 0)
                if (clientSocket == -1) {
                    println("Failed to create socket")
                    return@launch
                }

                val serverAddr = createSockaddrIn(device.port.toUShort(), inetAddr(device.ipAddress))

                if (connect(clientSocket, serverAddr.reinterpret(), sizeOf<sockaddr_in>().toUInt()) == -1) {
                    println("Failed to connect")
                    close(clientSocket)
                    return@launch
                }

                val messageObj = Message(
                    content = message,
                    sender = "Local Device"
                )
                val messageJson = json.encodeToString(messageObj)
                
                if (send(clientSocket, messageJson.refTo(0), messageJson.length.toULong(), 0) == -1L) {
                    println("Failed to send message")
                }

                close(clientSocket)
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
        if (serverSocket != -1) {
            close(serverSocket)
            serverSocket = -1
        }
    }
} 