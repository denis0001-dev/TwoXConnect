package ru.twoxconnect.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.twoxconnect.Res
import ru.twoxconnect.home
import ru.twoxconnect.network.DiscoveredDevice
import ru.twoxconnect.network.Message
import ru.twoxconnect.network.MessageService
import ru.twoxconnect.network.NetworkDiscovery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab() {
    TabBase {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    title = {
                        Text(
                            stringResource(Res.string.home),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                val networkDiscovery = NetworkDiscovery
                val messageService = MessageService()
                
                var isDiscovering by remember { mutableStateOf(false) }
                val discoveredDevices by networkDiscovery.discoveredDevices.collectAsState()
                val receivedMessages by messageService.receivedMessages.collectAsState()

                @Composable
                fun DeviceCard(
                    device: DiscoveredDevice,
                    onSendMessage: () -> Unit
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${device.ipAddress}:${device.port}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Button(
                                onClick = onSendMessage,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Send Test Message")
                            }
                        }
                    }
                }

                @Composable
                fun MessageCard(message: Message) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "From: ${message.sender}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Time: ${message.timestamp}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nearby Devices",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            isDiscovering = !isDiscovering
                            if (isDiscovering) {
                                networkDiscovery.startDiscovery()
                            } else {
                                networkDiscovery.stopDiscovery()
                            }
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(if (isDiscovering) "Stop Discovery" else "Start Discovery")
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(discoveredDevices) { device ->
                            DeviceCard(
                                device = device,
                                onSendMessage = { messageService.sendMessage(device, "test") }
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        text = "Received Messages",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(receivedMessages) { message ->
                            MessageCard(message = message)
                        }
                    }
                }
            }
        }
    }
}