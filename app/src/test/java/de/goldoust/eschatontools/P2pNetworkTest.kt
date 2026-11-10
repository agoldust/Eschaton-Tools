package de.goldoust.eschatontools

import de.goldoust.eschatontools.domain.p2p.DistanceCategory
import de.goldoust.eschatontools.domain.p2p.DistressLevel
import de.goldoust.eschatontools.domain.p2p.SosDistressPayload
import org.junit.Assert.assertEquals
import org.junit.Test

class P2pNetworkTest {

    @Test
    fun `DistanceCategory fromRssi calculates correct range category`() {
        assertEquals(DistanceCategory.IMMEDIATE, DistanceCategory.fromRssi(-50))
        assertEquals(DistanceCategory.NEAR, DistanceCategory.fromRssi(-65))
        assertEquals(DistanceCategory.MEDIUM, DistanceCategory.fromRssi(-80))
        assertEquals(DistanceCategory.FAR, DistanceCategory.fromRssi(-95))
    }

    @Test
    fun `SosDistressPayload creates payload with location and level`() {
        val payload = SosDistressPayload(
            latitude = 52.5200,
            longitude = 13.4050,
            altitude = 150.0,
            distressLevel = DistressLevel.CRITICAL_SOS,
            note = "Injured hiker near beacon"
        )

        assertEquals(52.5200, payload.latitude, 0.0001)
        assertEquals(13.4050, payload.longitude, 0.0001)
        assertEquals(150.0, payload.altitude, 0.1)
        assertEquals(DistressLevel.CRITICAL_SOS, payload.distressLevel)
        assertEquals("Injured hiker near beacon", payload.note)
    }

    @Test
    fun `DistressLevel labels are formatted properly`() {
        assertEquals("CRITICAL S.O.S.", DistressLevel.CRITICAL_SOS.label)
        assertEquals("MEDICAL EMERGENCY", DistressLevel.MEDICAL_EMERGENCY.label)
        assertEquals("SEARCH & RESCUE", DistressLevel.SEARCH_RESCUE.label)
        assertEquals("HAZARD ADVISORY", DistressLevel.HAZARD_WARNING.label)
    }
}
