package de.goldoust.eschatontools

import de.goldoust.eschatontools.domain.SolarCalculator
import de.goldoust.eschatontools.domain.Waypoint
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class SolarCalculatorTest {

    @Test
    fun calculateSolarPosition_equatorEquinox_daytime() {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(2026, Calendar.MARCH, 21, 12, 0, 0)
        }
        val pos = SolarCalculator.calculateSolarPosition(
            latitude = 0.0,
            longitude = 0.0,
            timestamp = cal.timeInMillis
        )

        // At solar noon at equator on equinox, elevation should be near 90 degrees
        assertTrue("Elevation should be > 80 degrees at noon", pos.elevation > 80f)
        assertTrue("Should be daytime", pos.isDaytime)
    }

    @Test
    fun waypoint_distanceAndBearing_correct() {
        // San Francisco (37.7749, -122.4194) to San Jose (37.3382, -121.8863)
        val sfLat = 37.7749
        val sfLon = -122.4194
        val sjWaypoint = Waypoint(
            name = "San Jose",
            latitude = 37.3382,
            longitude = -121.8863
        )

        val distMeters = sjWaypoint.distanceMeters(sfLat, sfLon)
        val bearing = sjWaypoint.bearingDegrees(sfLat, sfLon)

        // Distance SF to SJ is roughly 65 km (65,000 meters)
        assertTrue("Distance should be around 60-70km", distMeters in 60000f..70000f)
        // Bearing SF to SJ is roughly Southeast (around 120 - 150 degrees)
        assertTrue("Bearing should be Southeast", bearing in 120f..160f)
    }
}
