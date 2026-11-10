package de.goldoust.eschatontools.ui.compass

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.GeomagneticField
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val magneticDeclination: Float // Degrees E/W
)

class CompassLocationRepository(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationManager =
        context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    fun observeLocation(): Flow<LocationData> = callbackFlow {
        fun emitLocation(loc: Location) {
            val geoField = GeomagneticField(
                loc.latitude.toFloat(),
                loc.longitude.toFloat(),
                loc.altitude.toFloat(),
                System.currentTimeMillis()
            )

            trySend(
                LocationData(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    altitude = loc.altitude,
                    accuracy = loc.accuracy,
                    magneticDeclination = geoField.declination
                )
            )
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    emitLocation(location)
                }
            }
        }

        // Try Fused Location Client first
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000L
            ).setMinUpdateIntervalMillis(1000L).build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // Try getting last known location immediately
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                if (lastLoc != null) {
                    emitLocation(lastLoc)
                }
            }
        } catch (_: Exception) {
            // Fall back to LocationManager if Google Play Services fused location fails
            try {
                val listener = LocationListener { loc -> emitLocation(loc) }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2000L,
                        1f,
                        listener
                    )
                }
            } catch (_: Exception) {}
        }

        awaitClose {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            } catch (_: Exception) {}
        }
    }
}
