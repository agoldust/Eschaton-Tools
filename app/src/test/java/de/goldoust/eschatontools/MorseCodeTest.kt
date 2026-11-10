package de.goldoust.eschatontools

import de.goldoust.eschatontools.domain.beacon.MorseCode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MorseCodeTest {

    @Test
    fun `encodeToMorse encodes SOS correctly`() {
        val result = MorseCode.encodeToMorse("SOS")
        assertEquals("... --- ...", result)
    }

    @Test
    fun `encodeToMorse handles multi-word string`() {
        val result = MorseCode.encodeToMorse("HELP NOW")
        assertEquals(".... . .-.. .--.   -. --- .--", result)
    }

    @Test
    fun `calculateDotDurationMs returns expected ms for standard WPM`() {
        val dot15wpm = MorseCode.calculateDotDurationMs(15)
        assertEquals(80L, dot15wpm)

        val dot30wpm = MorseCode.calculateDotDurationMs(30)
        assertEquals(40L, dot30wpm)
    }

    @Test
    fun `encodeToTimings generates correct timing steps for SOS`() {
        val timings = MorseCode.encodeToTimings("SOS", 15)
        assertTrue(timings.isNotEmpty())

        // First symbol for 'S' should be a dot (ON for 80ms)
        val firstStep = timings.first()
        assertTrue(firstStep.isOn)
        assertEquals(80L, firstStep.durationMs)
    }
}
