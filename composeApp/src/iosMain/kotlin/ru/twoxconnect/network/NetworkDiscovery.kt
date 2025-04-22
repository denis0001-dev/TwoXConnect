package ru.twoxconnect.network

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.refTo
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.darwin.inet_ntoa
import platform.posix.AF_INET
import platform.posix.INADDR_ANY
import platform.posix.INADDR_BROADCAST
import platform.posix.SOCK_DGRAM
import platform.posix.SOL_SOCKET
import platform.posix.SO_BROADCAST
import platform.posix.bind
import platform.posix.close
import platform.posix.recvfrom
import platform.posix.sendto
import platform.posix.setsockopt
import platform.posix.sockaddr_in
import platform.posix.socket
import ru.denis0001dev.utils.htons
import ru.denis0001dev.utils.ntohs
import kotlin.time.Duration.Companion.seconds

actual object NetworkDiscovery {
    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    actual val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var isDiscovering = false
    private val DISCOVERY_PORT = 12345
    private val DISCOVERY_MESSAGE = "TWOXCONNECT_DISCOVERY"
    private val RESPONSE_MESSAGE = "TWOXCONNECT_RESPONSE"
    private var discoverySocket: Int = -1

    @OptIn(ExperimentalForeignApi::class)
    actual fun startDiscovery() {
        if (isDiscovering) return
        isDiscovering = true

        scope.launch {
            try {
                withContext(Dispatchers.Default) {
                    discoverySocket = socket(AF_INET, SOCK_DGRAM, 0)
                    if (discoverySocket == -1) {
                        println("Failed to create socket")
                        return@withContext
                    }

                    // Enable broadcast
                    val broadcast = nativeHeap.alloc<IntVar>().apply { value = 1 }
                    if (setsockopt(discoverySocket, SOL_SOCKET, SO_BROADCAST, broadcast.ptr, sizeOf<IntVar>().toUInt()) == -1) {
                        println("Failed to set broadcast option")
                        close(discoverySocket)
                        return@withContext
                    }

                    // Bind to any address
                    val serverAddr = createSockaddrIn(DISCOVERY_PORT.toUShort())
                    if (bind(discoverySocket, serverAddr.reinterpret(), sizeOf<sockaddr_in>().toUInt()) == -1) {
                        println("Failed to bind socket")
                        close(discoverySocket)
                        return@withContext
                    }

                    while (isDiscovering) {
                        // Send discovery message
                        val broadcastAddr = createSockaddrIn(DISCOVERY_PORT.toUShort(), INADDR_BROADCAST)
                        val message = DISCOVERY_MESSAGE.encodeToByteArray()
                        sendto(discoverySocket, message.refTo(0), message.size.toULong(), 0, 
                            broadcastAddr.reinterpret(), sizeOf<sockaddr_in>().toUInt())

                        // Wait for responses
                        val buffer = ByteArray(1024)
                        val clientAddr = nativeHeap.alloc<sockaddr_in>()
                        val addrLen = nativeHeap.alloc<UIntVar>().apply { value = sizeOf<sockaddr_in>().toUInt() }
                        
                        val bytesRead = recvfrom(discoverySocket, buffer.refTo(0), buffer.size.toULong(), 0,
                            clientAddr.ptr.reinterpret(), addrLen.ptr)

                        if (bytesRead > 0) {
                            val response = buffer.decodeToString(0, bytesRead.toInt())
                            if (response.startsWith(RESPONSE_MESSAGE)) {
                                val deviceName = response.substring(RESPONSE_MESSAGE.length + 1)
                                val ipAddress = inet_ntoa(clientAddr.sin_addr.readValue())?.toKString() ?: "unknown"
                                val port = ntohs(clientAddr.sin_port).toInt()

                                _discoveredDevices.update { list ->
                                    val device = DiscoveredDevice(deviceName, ipAddress, port)
                                    if (list.none { it.ipAddress == ipAddress && it.port == port }) {
                                        list + device
                                    } else {
                                        list
                                    }
                                }
                            }
                        }

                        kotlinx.coroutines.delay(1.seconds)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (discoverySocket != -1) {
                    close(discoverySocket)
                    discoverySocket = -1
                }
            }
        }
    }

    actual fun stopDiscovery() {
        isDiscovering = false
        if (discoverySocket != -1) {
            close(discoverySocket)
            discoverySocket = -1
        }
        _discoveredDevices.update { emptyList() }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun createSockaddrIn(port: UShort, address: UInt = INADDR_ANY): CPointer<sockaddr_in> {
        return nativeHeap.alloc<sockaddr_in>().apply {
            sin_family = AF_INET.toUByte()
            sin_port = htons(port)
            sin_addr.s_addr = address
        }.ptr
    }
} 