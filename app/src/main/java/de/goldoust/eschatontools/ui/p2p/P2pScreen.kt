package de.goldoust.eschatontools.ui.p2p

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.CellTower
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.goldoust.eschatontools.domain.p2p.DistressLevel
import de.goldoust.eschatontools.domain.p2p.NetworkMode
import de.goldoust.eschatontools.domain.p2p.P2pMessage
import de.goldoust.eschatontools.domain.p2p.P2pPeer
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.HazardYellow
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun P2pScreen(
    modifier: Modifier = Modifier,
    viewModel: P2pViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    var isPeerListExpanded by remember { mutableStateOf(true) }

    if (state.isSosDialogVisible) {
        SosBroadcastDialog(
            state = state,
            onDismiss = { viewModel.showSosDialog(false) },
            onSelectDistressLevel = { viewModel.setSelectedDistressLevel(it) },
            onNoteChange = { viewModel.setSosNote(it) },
            onConfirmBroadcast = { viewModel.triggerOneTapSosBroadcast() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Module Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CellTower,
                        contentDescription = null,
                        tint = TacticalAmberPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "P2P MESH & DIRECT CHAT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Infrastructure-Free Wi-Fi Direct & BLE",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { viewModel.clearChatHistory() }) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 1-Tap Emergency SOS Broadcast Button
        Button(
            onClick = { viewModel.showSosDialog(true) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmergencyRedTertiary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "1-TAP EMERGENCY S.O.S. BROADCAST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Broadcast GPS Location (${"%.4f".format(state.currentLatitude)}, ${"%.4f".format(state.currentLongitude)}) + Distress to All Peers",
                        fontSize = 11.sp,
                        color = Color.Black.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Active Mesh Status Bar & Mode Switcher
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WifiTethering,
                            contentDescription = null,
                            tint = if (state.connectedPeersCount > 0) TacticalGreenSecondary else HazardYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "MESH ACTIVE (${state.connectedPeersCount} PEERS CONNECTED)",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (state.connectedPeersCount > 0) TacticalGreenSecondary else HazardYellow
                        )
                    }

                    Button(
                        onClick = { viewModel.togglePeerDiscovery() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isSearchingPeers) TacticalAmberPrimary else MaterialTheme.colorScheme.surface,
                            contentColor = if (state.isSearchingPeers) Color.Black else TacticalAmberPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Radar,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.isSearchingPeers) "SCANNING..." else "SCAN PEERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Network Mode Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NetworkMode.entries.forEach { mode ->
                        FilterChip(
                            selected = state.networkMode == mode,
                            onClick = { viewModel.setNetworkMode(mode) },
                            label = { Text(mode.title, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalAmberPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // Local Peer Discovery List Drawer Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isPeerListExpanded = !isPeerListExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = TacticalAmberPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "DISCOVERED NEARBY NODES (${state.peerList.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = if (isPeerListExpanded) "[ HIDE ]" else "[ SHOW ]",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = TacticalAmberPrimary
            )
        }

        // Discovered Peers Horizontal List
        AnimatedVisibility(visible = isPeerListExpanded) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.peerList) { peer ->
                    PeerCard(
                        peer = peer,
                        onConnectClick = { viewModel.connectToPeer(peer.id) }
                    )
                }
            }
        }

        // Chat Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "OFFLINE MESH CHAT STREAM",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilterChip(
                selected = state.filterEmergencyOnly,
                onClick = { viewModel.setFilterEmergencyOnly(!state.filterEmergencyOnly) },
                label = { Text("EMERGENCY ONLY", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmergencyRedTertiary,
                    selectedLabelColor = Color.Black
                )
            )
        }

        // Message Feed
        val displayedMessages = if (state.filterEmergencyOnly) {
            state.messageHistory.filter { it.isEmergencySos }
        } else {
            state.messageHistory
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayedMessages) { msg ->
                ChatMessageItem(message = msg)
            }
        }

        // Quick Preset Tags
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val presets = listOf(
                "S.O.S. NEED ASSISTANCE",
                "MEDICAL HELP REQUIRED",
                "WATER & SUPPLIES NEEDED",
                "SECTOR CLEAR - POSITION OK"
            )
            items(presets) { preset ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable { viewModel.sendChatMessage(preset) }
                ) {
                    Text(
                        text = preset,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Broadcast Input Box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Broadcast mesh signal...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TacticalAmberPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendChatMessage(messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TacticalAmberPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send Message"
                )
            }
        }
    }
}

@Composable
fun PeerCard(
    peer: P2pPeer,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onConnectClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (peer.isConnected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = peer.deviceName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (peer.isConnected) TacticalGreenSecondary else Color.Gray, CircleShape)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SignalCellularAlt,
                    contentDescription = null,
                    tint = TacticalAmberPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${peer.rssi} dBm (${peer.distanceCategory.label})",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = peer.connectionType.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (peer.isConnected) "DISCONNECT" else "CONNECT",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (peer.isConnected) EmergencyRedTertiary else TacticalAmberPrimary
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: P2pMessage) {
    val dateStr = remember(message.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(message.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isEmergencySos)
                EmergencyRedTertiary.copy(alpha = 0.25f)
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (message.isEmergencySos) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = "Distress SOS",
                            tint = EmergencyRedTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (message.isEmergencySos) EmergencyRedTertiary else TacticalAmberPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Hops: ${message.hopCount}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (message.isEmergencySos) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (message.isEmergencySos && message.distressDetails != null) {
                val payload = message.distressDetails
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = HazardYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "GPS: Lat ${"%.5f".format(payload.latitude)}, Lon ${"%.5f".format(payload.longitude)} | Alt: ${"%.1f".format(payload.altitude)}m",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = HazardYellow
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SosBroadcastDialog(
    state: P2pUiState,
    onDismiss: () -> Unit,
    onSelectDistressLevel: (DistressLevel) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirmBroadcast: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirmBroadcast,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmergencyRedTertiary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "BROADCAST SOS NOW",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CANCEL", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = EmergencyRedTertiary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "1-TAP S.O.S. BROADCAST",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select distress protocol category and attach optional note. Your exact GPS coordinates and altitude will be attached automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Distress Category Selection
                DistressLevel.entries.filter { it != DistressLevel.NONE }.forEach { level ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDistressLevel(level) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.selectedDistressLevel == level,
                            onClick = { onSelectDistressLevel(level) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = EmergencyRedTertiary
                            )
                        )
                        Text(
                            text = level.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                OutlinedTextField(
                    value = state.sosNote,
                    onValueChange = onNoteChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Distress Note (Optional)") },
                    placeholder = { Text("e.g. Broken ankle near ridge") },
                    singleLine = true
                )

                // Location Preview Badge
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "LOCATION PAYLOAD:",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TacticalAmberPrimary
                        )
                        Text(
                            text = "Lat: ${"%.5f".format(state.currentLatitude)}, Lon: ${"%.5f".format(state.currentLongitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Altitude: ${"%.1f".format(state.currentAltitude)}m",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun P2pScreenPreview() {
    EschatonToolsTheme {
        P2pScreen()
    }
}
