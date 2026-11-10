package de.goldoust.eschatontools.ui.p2p

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.goldoust.eschatontools.domain.p2p.DistressLevel
import de.goldoust.eschatontools.domain.p2p.NetworkMode
import de.goldoust.eschatontools.domain.p2p.P2pMessage
import de.goldoust.eschatontools.domain.p2p.P2pNetworkManager
import de.goldoust.eschatontools.domain.p2p.P2pPeer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class P2pUiState(
    val isSearchingPeers: Boolean = false,
    val peerList: List<P2pPeer> = emptyList(),
    val connectedPeersCount: Int = 0,
    val messageHistory: List<P2pMessage> = emptyList(),
    val networkMode: NetworkMode = NetworkMode.HYBRID_BLE_WIFI,
    val selectedDistressLevel: DistressLevel = DistressLevel.CRITICAL_SOS,
    val sosNote: String = "",
    val isSosDialogVisible: Boolean = false,
    val filterEmergencyOnly: Boolean = false,
    val myNodeId: String = "",
    val myDeviceName: String = "",
    val currentLatitude: Double = 52.5200,
    val currentLongitude: Double = 13.4050,
    val currentAltitude: Double = 120.0,
    val lastSosBroadcastTime: Long? = null
)

class P2pViewModel(application: Application) : AndroidViewModel(application) {

    private val p2pNetworkManager = P2pNetworkManager(application)
    private val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _uiState = MutableStateFlow(
        P2pUiState(
            myNodeId = p2pNetworkManager.myNodeId,
            myDeviceName = p2pNetworkManager.myDeviceName
        )
    )
    val uiState: StateFlow<P2pUiState> = _uiState.asStateFlow()

    init {
        observeNetworkManager()
        obtainCurrentLocation()
        // Start initial peer discovery
        p2pNetworkManager.startPeerDiscovery(_uiState.value.networkMode)
    }

    private fun observeNetworkManager() {
        viewModelScope.launch {
            p2pNetworkManager.discoveredPeers.collect { peers ->
                _uiState.update {
                    it.copy(
                        peerList = peers,
                        connectedPeersCount = peers.count { p -> p.isConnected }
                    )
                }
            }
        }

        viewModelScope.launch {
            p2pNetworkManager.incomingMessages.collect { messages ->
                _uiState.update { it.copy(messageHistory = messages) }
            }
        }

        viewModelScope.launch {
            p2pNetworkManager.isDiscovering.collect { isDiscovering ->
                _uiState.update { it.copy(isSearchingPeers = isDiscovering) }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtainCurrentLocation() {
        try {
            val locManager = locationManager ?: return
            val lastLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            lastLoc?.let { updateLocationState(it) }

            locManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        updateLocationState(location)
                    }
                },
                null
            )
        } catch (e: Exception) {
            // Location permission or hardware disabled
        }
    }

    private fun updateLocationState(location: Location) {
        _uiState.update {
            it.copy(
                currentLatitude = location.latitude,
                currentLongitude = location.longitude,
                currentAltitude = location.altitude
            )
        }
    }

    fun togglePeerDiscovery() {
        if (_uiState.value.isSearchingPeers) {
            p2pNetworkManager.stopPeerDiscovery()
        } else {
            p2pNetworkManager.startPeerDiscovery(_uiState.value.networkMode)
        }
    }

    fun setNetworkMode(mode: NetworkMode) {
        _uiState.update { it.copy(networkMode = mode) }
        p2pNetworkManager.startPeerDiscovery(mode)
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        p2pNetworkManager.sendChatMessage(text.trim())
    }

    fun setSosNote(note: String) {
        _uiState.update { it.copy(sosNote = note) }
    }

    fun setSelectedDistressLevel(level: DistressLevel) {
        _uiState.update { it.copy(selectedDistressLevel = level) }
    }

    fun showSosDialog(show: Boolean) {
        _uiState.update { it.copy(isSosDialogVisible = show) }
    }

    fun triggerOneTapSosBroadcast() {
        val s = _uiState.value
        p2pNetworkManager.broadcastSosAlert(
            distressLevel = s.selectedDistressLevel,
            latitude = s.currentLatitude,
            longitude = s.currentLongitude,
            altitude = s.currentAltitude,
            note = s.sosNote
        )
        _uiState.update {
            it.copy(
                lastSosBroadcastTime = System.currentTimeMillis(),
                isSosDialogVisible = false
            )
        }
    }

    fun setFilterEmergencyOnly(enabled: Boolean) {
        _uiState.update { it.copy(filterEmergencyOnly = enabled) }
    }

    fun connectToPeer(peerId: String) {
        p2pNetworkManager.connectToPeer(peerId)
    }

    fun clearChatHistory() {
        p2pNetworkManager.clearMessages()
    }

    override fun onCleared() {
        super.onCleared()
        p2pNetworkManager.stopPeerDiscovery()
    }
}
