package de.goldoust.eschatontools.domain

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Waypoint(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Calculates distance in meters from current latitude/longitude to this waypoint
     * using the Haversine formula.
     */
    fun distanceMeters(currentLat: Double, currentLon: Double): Float {
        val r = 6371000.0 // Earth's radius in meters
        val lat1Rad = Math.toRadians(currentLat)
        val lat2Rad = Math.toRadians(latitude)
        val dLatRad = Math.toRadians(latitude - currentLat)
        val dLonRad = Math.toRadians(longitude - currentLon)

        val a = sin(dLatRad / 2) * sin(dLatRad / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(dLonRad / 2) * sin(dLonRad / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (r * c).toFloat()
    }

    /**
     * Calculates initial bearing (azimuth in degrees 0..360) from current location to this waypoint.
     */
    fun bearingDegrees(currentLat: Double, currentLon: Double): Float {
        val lat1Rad = Math.toRadians(currentLat)
        val lat2Rad = Math.toRadians(latitude)
        val dLonRad = Math.toRadians(longitude - currentLon)

        val y = sin(dLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLonRad)

        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
        if (bearing < 0) bearing += 360f
        return bearing
    }
}
