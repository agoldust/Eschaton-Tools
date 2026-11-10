package de.goldoust.eschatontools.domain.p2p

enum class DistressLevel(val label: String) {
    NONE("Normal"),
    CRITICAL_SOS("CRITICAL S.O.S."),
    MEDICAL_EMERGENCY("MEDICAL EMERGENCY"),
    SEARCH_RESCUE("SEARCH & RESCUE"),
    HAZARD_WARNING("HAZARD ADVISORY")
}

enum class P2pConnectionType(val displayName: String) {
    WIFI_DIRECT("Wi-Fi Direct"),
    BLUETOOTH_LE("BLE Mesh"),
    HYBRID_RELAY("Hybrid Node")
}

enum class DistanceCategory(val label: String) {
    IMMEDIATE("< 5m"),
    NEAR("5 - 25m"),
    MEDIUM("25 - 75m"),
    FAR("> 75m");

    companion object {
        fun fromRssi(rssi: Int): DistanceCategory = when {
            rssi >= -55 -> IMMEDIATE
            rssi >= -70 -> NEAR
            rssi >= -85 -> MEDIUM
            else -> FAR
        }
    }
}

data class P2pPeer(
    val id: String,
    val deviceName: String,
    val address: String,
    val connectionType: P2pConnectionType,
    val rssi: Int,
    val distanceCategory: DistanceCategory = DistanceCategory.fromRssi(rssi),
    val isConnected: Boolean = false,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val distressStatus: DistressLevel = DistressLevel.NONE
)

data class SosDistressPayload(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val distressLevel: DistressLevel,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class P2pMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isEmergencySos: Boolean = false,
    val distressDetails: SosDistressPayload? = null,
    val hopCount: Int = 1,
    val signalRssi: Int = -60
)

enum class NetworkMode(val title: String) {
    HYBRID_BLE_WIFI("Wi-Fi Direct + BLE Mesh"),
    WIFI_DIRECT_ONLY("Wi-Fi Direct Only"),
    BLE_ONLY("BLE Mesh Only")
}
