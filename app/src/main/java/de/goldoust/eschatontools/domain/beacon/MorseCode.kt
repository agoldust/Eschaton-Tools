package de.goldoust.eschatontools.domain.beacon

data class MorseTimingStep(
    val isOn: Boolean,
    val durationMs: Long
)

object MorseCode {
    private val MORSE_MAP = mapOf(
        'A' to ".-",    'B' to "-...",  'C' to "-.-.",  'D' to "-..",
        'E' to ".",     'F' to "..-.",  'G' to "--.",   'H' to "....",
        'I' to "..",    'J' to ".---",  'K' to "-.-",   'L' to ".-..",
        'M' to "--",    'N' to "-.",    'O' to "---",   'P' to ".--.",
        'Q' to "--.-",  'R' to ".-.",   'S' to "...",   'T' to "-",
        'U' to "..-",   'V' to "...-",  'W' to ".--",   'X' to "-..-",
        'Y' to "-.--",  'Z' to "--..",
        '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
        '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..",
        '9' to "----.", '0' to "-----"
    )

    /**
     * Converts raw text string into standard Morse representation (e.g. "SOS" -> "... --- ...").
     */
    fun encodeToMorse(text: String): String {
        val uppercaseText = text.trim().uppercase()
        val words = uppercaseText.split(Regex("\\s+"))

        return words.joinToString("   ") { word ->
            word.mapNotNull { char -> MORSE_MAP[char] }
                .joinToString(" ")
        }
    }

    /**
     * Calculates dot duration in milliseconds from Words Per Minute (WPM).
     * Standard formula: PARIS standard = 50 dot units per word.
     * dotMs = 1200 / WPM.
     */
    fun calculateDotDurationMs(wpm: Int): Long {
        val safeWpm = wpm.coerceIn(1, 60)
        return (1200L / safeWpm).coerceAtLeast(20L)
    }

    /**
     * Generates a sequence of ON/OFF timing steps for flashing or audio tone playback.
     * Timing rules:
     * - Dot = 1 unit ON
     * - Dash = 3 units ON
     * - Space between dots/dashes in same char = 1 unit OFF
     * - Space between chars = 3 units OFF
     * - Space between words = 7 units OFF
     */
    fun encodeToTimings(text: String, wpm: Int = 15): List<MorseTimingStep> {
        val dotMs = calculateDotDurationMs(wpm)
        val dashMs = dotMs * 3
        val intraSymbolOffMs = dotMs
        val interCharOffMs = dotMs * 3
        val wordOffMs = dotMs * 7

        val result = mutableListOf<MorseTimingStep>()
        val uppercaseText = text.trim().uppercase()

        if (uppercaseText.isEmpty()) return emptyList()

        val words = uppercaseText.split(Regex("\\s+"))

        for ((wordIndex, word) in words.withIndex()) {
            if (wordIndex > 0) {
                // Word separation
                result.add(MorseTimingStep(isOn = false, durationMs = wordOffMs))
            }

            for ((charIndex, char) in word.withIndex()) {
                val morsePattern = MORSE_MAP[char] ?: continue

                if (charIndex > 0) {
                    // Character separation
                    result.add(MorseTimingStep(isOn = false, durationMs = interCharOffMs))
                }

                for ((symbolIndex, symbol) in morsePattern.withIndex()) {
                    if (symbolIndex > 0) {
                        // Intra-character symbol separation
                        result.add(MorseTimingStep(isOn = false, durationMs = intraSymbolOffMs))
                    }

                    when (symbol) {
                        '.' -> result.add(MorseTimingStep(isOn = true, durationMs = dotMs))
                        '-' -> result.add(MorseTimingStep(isOn = true, durationMs = dashMs))
                    }
                }
            }
        }

        return result
    }
}
