package de.goldoust.eschatontools.ui.compass

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.goldoust.eschatontools.domain.SolarCalculator
import de.goldoust.eschatontools.domain.Waypoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class CompassUiState(
    val magneticAzimuth: Float = 0f,
    val displayAzimuth: Float = 0f,
    val cardinalDirection: String = "N",
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val isTrueNorth: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val declination: Float = 0f,
    val sunAzimuth: Float? = null,
    val sunElevation: Float? = null,
    val isDaytime: Boolean = false,
    val waypoints: List<Waypoint> = emptyList(),
    val activeWaypoint: Waypoint? = null,
    val distanceToActiveWaypoint: Float? = null,
    val bearingToActiveWaypoint: Float? = null
)

class CompassViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CompassUiState())
    val uiState: StateFlow<CompassUiState> = _uiState.asStateFlow()

    private val sensorRepository = CompassSensorRepository(application)
    private val locationRepository = CompassLocationRepository(application)

    init {
        // Observe live sensor data
        sensorRepository.observeOrientation()
            .onEach { sensorData ->
                _uiState.update { current ->
                    val declinationOffset = if (current.isTrueNorth) current.declination else 0f
                    var displayAz = (sensorData.azimuth + declinationOffset) % 360f
                    if (displayAz < 0) displayAz += 360f

                    val cardinal = getCardinalDirection(displayAz)

                    var dist: Float? = null
                    var bearing: Float? = null
                    if (current.activeWaypoint != null && current.latitude != null && current.longitude != null) {
                        dist = current.activeWaypoint.distanceMeters(current.latitude, current.longitude)
                        bearing = current.activeWaypoint.bearingDegrees(current.latitude, current.longitude)
                    }

                    current.copy(
                        magneticAzimuth = sensorData.azimuth,
                        displayAzimuth = displayAz,
                        cardinalDirection = cardinal,
                        pitch = sensorData.pitch,
                        roll = sensorData.roll,
                        distanceToActiveWaypoint = dist,
                        bearingToActiveWaypoint = bearing
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)

        // Observe live GPS location data
        locationRepository.observeLocation()
            .onEach { loc ->
                _uiState.update { current ->
                    val solarPos = SolarCalculator.calculateSolarPosition(
                        latitude = loc.latitude,
                        longitude = loc.longitude
                    )

                    val declinationOffset = if (current.isTrueNorth) loc.magneticDeclination else 0f
                    var displayAz = (current.magneticAzimuth + declinationOffset) % 360f
                    if (displayAz < 0) displayAz += 360f

                    var dist: Float? = null
                    var bearing: Float? = null
                    if (current.activeWaypoint != null) {
                        dist = current.activeWaypoint.distanceMeters(loc.latitude, loc.longitude)
                        bearing = current.activeWaypoint.bearingDegrees(loc.latitude, loc.longitude)
                    }

                    current.copy(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        altitude = loc.altitude,
                        accuracy = loc.accuracy,
                        declination = loc.magneticDeclination,
                        displayAzimuth = displayAz,
                        sunAzimuth = solarPos.azimuth,
                        sunElevation = solarPos.elevation,
                        isDaytime = solarPos.isDaytime,
                        distanceToActiveWaypoint = dist,
                        bearingToActiveWaypoint = bearing
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun toggleTrueNorth() {
        _uiState.update { current ->
            val nextIsTrueNorth = !current.isTrueNorth
            val declinationOffset = if (nextIsTrueNorth) current.declination else 0f
            var displayAz = (current.magneticAzimuth + declinationOffset) % 360f
            if (displayAz < 0) displayAz += 360f

            current.copy(
                isTrueNorth = nextIsTrueNorth,
                displayAzimuth = displayAz,
                cardinalDirection = getCardinalDirection(displayAz)
            )
        }
    }

    fun addWaypoint(name: String) {
        val current = _uiState.value
        val lat = current.latitude ?: return
        val lon = current.longitude ?: return
        val newWp = Waypoint(
            name = name.ifBlank { "Waypoint ${_uiState.value.waypoints.size + 1}" },
            latitude = lat,
            longitude = lon,
            altitude = current.altitude
        )
        _uiState.update { state ->
            val updatedList = state.waypoints + newWp
            state.copy(
                waypoints = updatedList,
                activeWaypoint = newWp,
                distanceToActiveWaypoint = newWp.distanceMeters(lat, lon),
                bearingToActiveWaypoint = newWp.bearingDegrees(lat, lon)
            )
        }
    }

    fun selectActiveWaypoint(waypoint: Waypoint?) {
        _uiState.update { state ->
            var dist: Float? = null
            var bearing: Float? = null
            if (waypoint != null && state.latitude != null && state.longitude != null) {
                dist = waypoint.distanceMeters(state.latitude, state.longitude)
                bearing = waypoint.bearingDegrees(state.latitude, state.longitude)
            }
            state.copy(
                activeWaypoint = waypoint,
                distanceToActiveWaypoint = dist,
                bearingToActiveWaypoint = bearing
            )
        }
    }

    fun deleteWaypoint(waypoint: Waypoint) {
        _uiState.update { state ->
            val updatedList = state.waypoints.filter { it.id != waypoint.id }
            val nextActive = if (state.activeWaypoint?.id == waypoint.id) null else state.activeWaypoint
            state.copy(
                waypoints = updatedList,
                activeWaypoint = nextActive
            )
        }
    }

    private fun getCardinalDirection(azimuth: Float): String {
        val normalized = (azimuth % 360 + 360) % 360
        return when {
            normalized >= 337.5 || normalized < 22.5 -> "N"
            normalized >= 22.5 && normalized < 67.5 -> "NE"
            normalized >= 67.5 && normalized < 112.5 -> "E"
            normalized >= 112.5 && normalized < 157.5 -> "SE"
            normalized >= 157.5 && normalized < 202.5 -> "S"
            normalized >= 202.5 && normalized < 247.5 -> "SW"
            normalized >= 247.5 && normalized < 292.5 -> "W"
            normalized >= 292.5 && normalized < 337.5 -> "NW"
            else -> "N"
        }
    }
}
