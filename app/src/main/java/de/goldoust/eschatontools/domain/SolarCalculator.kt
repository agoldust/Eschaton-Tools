package de.goldoust.eschatontools.domain

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class SolarPosition(
    val azimuth: Float,    // 0..360 degrees (North = 0, East = 90, South = 180, West = 270)
    val elevation: Float,  // -90..+90 degrees (above horizon if > 0)
    val isDaytime: Boolean
)

object SolarCalculator {

    /**
     * Calculates the solar position (Azimuth & Elevation) offline given latitude, longitude,
     * and epoch timestamp in milliseconds.
     *
     * Algorithm based on NOAA / Astronomical Solar Position formulas.
     */
    fun calculateSolarPosition(
        latitude: Double,
        longitude: Double,
        timestamp: Long = System.currentTimeMillis()
    ): SolarPosition {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = timestamp
        }

        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Fractional hour in UTC
        val utcHours = hour + minute / 60.0 + second / 3600.0

        // Fractional year gamma in radians
        val gamma = (2.0 * Math.PI / 365.0) * (dayOfYear - 1 + (utcHours - 12.0) / 24.0)

        // Equation of Time (EOT) in minutes
        val eqTime = 229.18 * (
                0.000075 +
                        0.001868 * cos(gamma) -
                        0.032077 * sin(gamma) -
                        0.014615 * cos(2 * gamma) -
                        0.040849 * sin(2 * gamma)
                )

        // Solar Declination (delta) in radians
        val declination = 0.006918 -
                0.399912 * cos(gamma) +
                0.070257 * sin(gamma) -
                0.006758 * cos(2 * gamma) +
                0.000907 * sin(2 * gamma) -
                0.002697 * cos(3 * gamma) +
                0.00148 * sin(3 * gamma)

        // True Solar Time (TST) in minutes
        var tst = utcHours * 60.0 + eqTime + (4.0 * longitude)
        tst = (tst % 1440.0 + 1440.0) % 1440.0

        // Hour Angle (HA) in degrees and radians
        val haDeg = (tst / 4.0) - 180.0
        val haRad = Math.toRadians(haDeg)
        val latRad = Math.toRadians(latitude)

        // Solar Zenith Angle (theta_z)
        var cosZenith = sin(latRad) * sin(declination) + cos(latRad) * cos(declination) * cos(haRad)
        cosZenith = cosZenith.coerceIn(-1.0, 1.0)
        val zenithRad = acos(cosZenith)
        val elevationDeg = 90.0 - Math.toDegrees(zenithRad)

        // Solar Azimuth Angle (azimuthRad)
        val sinZenith = sin(zenithRad)
        var azimuthDeg = if (sinZenith > 0.0001) {
            var cosAz = (sin(declination) * cos(latRad) - cos(declination) * sin(latRad) * cos(haRad)) / sinZenith
            cosAz = cosAz.coerceIn(-1.0, 1.0)
            val az = Math.toDegrees(acos(cosAz))
            if (haDeg > 0) 360.0 - az else az
        } else {
            180.0
        }

        azimuthDeg = (azimuthDeg % 360.0 + 360.0) % 360.0

        return SolarPosition(
            azimuth = azimuthDeg.toFloat(),
            elevation = elevationDeg.toFloat(),
            isDaytime = elevationDeg > -0.833 // -0.833° accounts for atmospheric refraction at horizon
        )
    }
}
