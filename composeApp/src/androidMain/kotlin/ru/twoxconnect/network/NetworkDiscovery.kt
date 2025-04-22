package ru.twoxconnect.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.time.Duration.Companion.seconds

actual object NetworkDiscovery {
    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    actual val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var discoverySocket: DatagramSocket? = null
    private var isDiscovering = false
    private val DISCOVERY_PORT = 12345
    private val DISCOVERY_MESSAGE = "TWOXCONNECT_DISCOVERY"
    private val RESPONSE_MESSAGE = "TWOXCONNECT_RESPONSE"

    actual fun startDiscovery() {
        if (isDiscovering) return
        isDiscovering = true

        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    discoverySocket = DatagramSocket(DISCOVERY_PORT)
                    discoverySocket?.broadcast = true

                    while (isDiscovering) {
                        // Send discovery message
                        val broadcastAddress = InetAddress.getByName("255.255.255.255")
                        val sendData = DISCOVERY_MESSAGE.toByteArray()
                        val sendPacket = DatagramPacket(sendData, sendData.size, broadcastAddress, DISCOVERY_PORT)
                        discoverySocket?.send(sendPacket)

                        // Wait for responses
                        val receiveData = ByteArray(1024)
                        val receivePacket = DatagramPacket(receiveData, receiveData.size)
                        discoverySocket?.receive(receivePacket)

                        val response = String(receivePacket.data, 0, receivePacket.length)
                        if (response.startsWith(RESPONSE_MESSAGE)) {
                            val deviceName = response.substring(RESPONSE_MESSAGE.length + 1)
                            val ipAddress = receivePacket.address.hostAddress!!
                            val port = receivePacket.port

                            _discoveredDevices.update { list ->
                                val device = DiscoveredDevice(deviceName, ipAddress, port)
                                if (list.none { it.ipAddress == ipAddress && it.port == port }) {
                                    list + device
                                } else {
                                    list
                                }
                            }
                        }

                        kotlinx.coroutines.delay(1.seconds)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                discoverySocket?.close()
                discoverySocket = null
            }
        }
    }

    actual fun stopDiscovery() {
        isDiscovering = false
        discoverySocket?.close()
        discoverySocket = null
        _discoveredDevices.update { emptyList() }
    }
} 