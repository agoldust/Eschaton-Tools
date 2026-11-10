package de.goldoust.eschatontools.domain.p2p

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class P2pNetworkManager(
    private val context: Context
) {
    private val wifiP2pManager: WifiP2pManager? by lazy {
        try {
            context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
        } catch (e: Exception) {
            null
        }
    }

    private var wifiP2pChannel: WifiP2pManager.Channel? = null

    private val _discoveredPeers = MutableStateFlow<List<P2pPeer>>(emptyList())
    val discoveredPeers: StateFlow<List<P2pPeer>> = _discoveredPeers.asStateFlow()

    private val _incomingMessages = MutableStateFlow<List<P2pMessage>>(emptyList())
    val incomingMessages: StateFlow<List<P2pMessage>> = _incomingMessages.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var discoveryJob: Job? = null

    val myNodeId: String = "NODE-" + UUID.randomUUID().toString().take(6).uppercase()
    val myDeviceName: String = "ESCHATON-" + android.os.Build.MODEL.take(8).uppercase()

    init {
        initializeWifiP2p()
        loadInitialSeedData()
    }

    private fun initializeWifiP2p() {
        try {
            val manager = wifiP2pManager ?: return
            wifiP2pChannel = manager.initialize(context, context.mainLooper, null)
        } catch (e: Exception) {
            // Wi-Fi P2P not available or disabled
        }
    }

    private fun loadInitialSeedData() {
        val initialPeers = listOf(
            P2pPeer(
                id = "NODE-0x8A12",
                deviceName = "ALPHA-NODE",
                address = "192.168.49.12",
                connectionType = P2pConnectionType.WIFI_DIRECT,
                rssi = -52,
                isConnected = true,
                distressStatus = DistressLevel.NONE
            ),
            P2pPeer(
                id = "NODE-0x9F44",
                deviceName = "BRAVO-NODE",
                address = "192.168.49.44",
                connectionType = P2pConnectionType.BLUETOOTH_LE,
                rssi = -74,
                isConnected = true,
                distressStatus = DistressLevel.NONE
            ),
            P2pPeer(
                id = "NODE-0x3C89",
                deviceName = "CHARLIE-RELAY",
                address = "192.168.49.89",
                connectionType = P2pConnectionType.HYBRID_RELAY,
                rssi = -88,
                isConnected = false,
                distressStatus = DistressLevel.NONE
            )
        )

        val initialMessages = listOf(
            P2pMessage(
                id = UUID.randomUUID().toString(),
                senderId = "NODE-0x8A12",
                senderName = "ALPHA-NODE",
                content = "Local off-grid mesh initialized. Signal strong.",
                timestamp = System.currentTimeMillis() - 360000L,
                isEmergencySos = false
            ),
            P2pMessage(
                id = UUID.randomUUID().toString(),
                senderId = "NODE-0x9F44",
                senderName = "BRAVO-NODE",
                content = "Connected via BLE mesh relay. Sector 4 waypoint synchronized.",
                timestamp = System.currentTimeMillis() - 180000L,
                isEmergencySos = false
            )
        )

        _discoveredPeers.value = initialPeers
        _incomingMessages.value = initialMessages
    }

    @SuppressLint("MissingPermission")
    fun startPeerDiscovery(mode: NetworkMode = NetworkMode.HYBRID_BLE_WIFI) {
        _isDiscovering.value = true

        try {
            val manager = wifiP2pManager
            val channel = wifiP2pChannel
            if (manager != null && channel != null && (mode == NetworkMode.HYBRID_BLE_WIFI || mode == NetworkMode.WIFI_DIRECT_ONLY)) {
                manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {}
                    override fun onFailure(reason: Int) {}
                })
            }
        } catch (e: Exception) {
            // Ignore hardware discovery exception
        }

        // Simulate local network signal sweeps & discovering dynamic nearby nodes
        discoveryJob?.cancel()
        discoveryJob = scope.launch {
            while (isActive && _isDiscovering.value) {
                delay(3000L)
                updateSimulatedPeerSignals()
            }
        }
    }

    fun stopPeerDiscovery() {
        _isDiscovering.value = false
        discoveryJob?.cancel()
        discoveryJob = null
    }

    private fun updateSimulatedPeerSignals() {
        _discoveredPeers.update { currentPeers ->
            currentPeers.map { peer ->
                // Random RSSI fluctuation +/- 4 dBm
                val delta = (-4..4).random()
                val newRssi = (peer.rssi + delta).coerceIn(-95, -45)
                peer.copy(
                    rssi = newRssi,
                    distanceCategory = DistanceCategory.fromRssi(newRssi),
                    lastSeenTimestamp = System.currentTimeMillis()
                )
            }
        }
    }

    fun sendChatMessage(content: String): P2pMessage {
        val msg = P2pMessage(
            id = UUID.randomUUID().toString(),
            senderId = myNodeId,
            senderName = "$myDeviceName (YOU)",
            content = content,
            timestamp = System.currentTimeMillis(),
            isEmergencySos = false,
            signalRssi = -45
        )

        _incomingMessages.update { it + msg }
        return msg
    }

    fun broadcastSosAlert(
        distressLevel: DistressLevel,
        latitude: Double,
        longitude: Double,
        altitude: Double,
        note: String
    ): P2pMessage {
        val payload = SosDistressPayload(
            latitude = latitude,
            longitude = longitude,
            altitude = altitude,
            distressLevel = distressLevel,
            note = note,
            timestamp = System.currentTimeMillis()
        )

        val emergencyContent = "[${distressLevel.label}] Lat: ${"%.5f".format(latitude)}, Lon: ${"%.5f".format(longitude)}, Alt: ${"%.1f".format(altitude)}m. ${note.ifBlank { "Immediate assistance requested!" }}"

        val sosMsg = P2pMessage(
            id = UUID.randomUUID().toString(),
            senderId = myNodeId,
            senderName = "$myDeviceName (DISTRESS)",
            content = emergencyContent,
            timestamp = System.currentTimeMillis(),
            isEmergencySos = true,
            distressDetails = payload,
            hopCount = 1,
            signalRssi = -40
        )

        _incomingMessages.update { it + sosMsg }

        // Trigger simulated response from nearby peer if SOS broadcasted
        scope.launch {
            delay(2000L)
            val responder = _discoveredPeers.value.firstOrNull { it.isConnected } ?: return@launch
            val responseMsg = P2pMessage(
                id = UUID.randomUUID().toString(),
                senderId = responder.id,
                senderName = responder.deviceName,
                content = "ACK SOS distress broadcast from $myDeviceName! Signal received at RSSI ${responder.rssi}dBm. Relaying to wider mesh.",
                timestamp = System.currentTimeMillis(),
                isEmergencySos = false,
                hopCount = 2,
                signalRssi = responder.rssi
            )
            _incomingMessages.update { it + responseMsg }
        }

        return sosMsg
    }

    fun connectToPeer(peerId: String) {
        _discoveredPeers.update { current ->
            current.map {
                if (it.id == peerId) it.copy(isConnected = !it.isConnected) else it
            }
        }
    }

    fun clearMessages() {
        _incomingMessages.value = emptyList()
    }
}
