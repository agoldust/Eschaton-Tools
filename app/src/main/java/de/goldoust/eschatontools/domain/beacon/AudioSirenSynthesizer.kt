package de.goldoust.eschatontools.domain.beacon

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class SirenMode {
    OFF,
    WAIL,
    HI_LO,
    AUDIO_MORSE_SOS,
    EMERGENCY_HORN
}

class AudioSirenSynthesizer {

    private var audioTrack: AudioTrack? = null
    private var synthesisJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    private var currentVolume = 1.0f

    private val sampleRate = 44100

    fun playSiren(mode: SirenMode, volume: Float = 1.0f, morseWpm: Int = 15) {
        stop()
        if (mode == SirenMode.OFF) return

        currentVolume = volume.coerceIn(0.0f, 1.0f)

        synthesisJob = scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(4096)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()
                setAudioTrackVolume(track, currentVolume)

                when (mode) {
                    SirenMode.WAIL -> runWailSiren(track)
                    SirenMode.HI_LO -> runHiLoSiren(track)
                    SirenMode.AUDIO_MORSE_SOS -> runAudioMorseSos(track, morseWpm)
                    SirenMode.EMERGENCY_HORN -> runEmergencyHorn(track)
                    SirenMode.OFF -> {}
                }
            } catch (e: Exception) {
                // Safeguard against missing audio output hardware or track init failure
            } finally {
                cleanUpTrack()
            }
        }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 1.0f)
        audioTrack?.let { setAudioTrackVolume(it, currentVolume) }
    }

    private fun setAudioTrackVolume(track: AudioTrack, vol: Float) {
        try {
            track.setVolume(vol)
        } catch (e: Exception) {
            // Ignore volume setting errors on legacy hardware
        }
    }

    /**
     * Smooth frequency sweep from 400Hz to 1200Hz back and forth.
     */
    private fun runWailSiren(track: AudioTrack) {
        val bufferSize = 1024
        val buffer = ShortArray(bufferSize)
        var phase = 0.0
        val minFreq = 400.0
        val maxFreq = 1200.0
        val sweepPeriodSec = 3.5
        var time = 0.0

        while (synthesisJob?.isActive == true) {
            for (i in 0 until bufferSize) {
                time += 1.0 / sampleRate
                // Triangle wave modulation for smooth siren sweep
                val cyclePos = (time % sweepPeriodSec) / sweepPeriodSec
                val currentFreq = if (cyclePos < 0.5) {
                    minFreq + (maxFreq - minFreq) * (cyclePos * 2.0)
                } else {
                    maxFreq - (maxFreq - minFreq) * ((cyclePos - 0.5) * 2.0)
                }

                phase += 2.0 * Math.PI * currentFreq / sampleRate
                if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI

                val sample = (sin(phase) * 28000).toInt().coerceIn(-32767, 32767)
                buffer[i] = sample.toShort()
            }
            track.write(buffer, 0, bufferSize)
        }
    }

    /**
     * Alternates between 700Hz and 900Hz tones every 400ms.
     */
    private fun runHiLoSiren(track: AudioTrack) {
        val bufferSize = 1024
        val buffer = ShortArray(bufferSize)
        var phase = 0.0
        val lowFreq = 700.0
        val highFreq = 950.0
        val intervalSec = 0.4
        var time = 0.0

        while (synthesisJob?.isActive == true) {
            for (i in 0 until bufferSize) {
                time += 1.0 / sampleRate
                val isHigh = ((time / intervalSec).toInt() % 2) == 0
                val freq = if (isHigh) highFreq else lowFreq

                phase += 2.0 * Math.PI * freq / sampleRate
                if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI

                val sample = (sin(phase) * 28000).toInt().coerceIn(-32767, 32767)
                buffer[i] = sample.toShort()
            }
            track.write(buffer, 0, bufferSize)
        }
    }

    /**
     * Synthesizes audio Morse SOS using 800Hz tone.
     */
    private fun runAudioMorseSos(track: AudioTrack, morseWpm: Int) {
        val timings = MorseCode.encodeToTimings("SOS", morseWpm)
        if (timings.isEmpty()) return

        val toneFreq = 800.0
        val bufferSize = 512

        while (synthesisJob?.isActive == true) {
            for (step in timings) {
                if (synthesisJob?.isActive != true) break
                val numSamples = ((step.durationMs / 1000.0) * sampleRate).toInt()
                var samplesWritten = 0

                var phase = 0.0
                while (samplesWritten < numSamples && synthesisJob?.isActive == true) {
                    val count = (numSamples - samplesWritten).coerceAtMost(bufferSize)
                    val buffer = ShortArray(count)

                    for (i in 0 until count) {
                        if (step.isOn) {
                            phase += 2.0 * Math.PI * toneFreq / sampleRate
                            if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI
                            buffer[i] = (sin(phase) * 28000).toInt().toShort()
                        } else {
                            buffer[i] = 0
                        }
                    }
                    track.write(buffer, 0, count)
                    samplesWritten += count
                }
            }
            // End of Morse loop pause (1.5s)
            val pauseSamples = (1.5 * sampleRate).toInt()
            var silenceWritten = 0
            val silenceBuffer = ShortArray(bufferSize)
            while (silenceWritten < pauseSamples && synthesisJob?.isActive == true) {
                val count = (pauseSamples - silenceWritten).coerceAtMost(bufferSize)
                track.write(silenceBuffer, 0, count)
                silenceWritten += count
            }
        }
    }

    /**
     * Emergency dual-frequency horn blast (300Hz + 465Hz).
     */
    private fun runEmergencyHorn(track: AudioTrack) {
        val bufferSize = 1024
        val buffer = ShortArray(bufferSize)
        var phase1 = 0.0
        var phase2 = 0.0
        val freq1 = 310.0
        val freq2 = 465.0

        while (synthesisJob?.isActive == true) {
            for (i in 0 until bufferSize) {
                phase1 += 2.0 * Math.PI * freq1 / sampleRate
                if (phase1 > 2.0 * Math.PI) phase1 -= 2.0 * Math.PI

                phase2 += 2.0 * Math.PI * freq2 / sampleRate
                if (phase2 > 2.0 * Math.PI) phase2 -= 2.0 * Math.PI

                // Combine both tones for harsh horn acoustic blast
                val sample1 = sin(phase1) * 16000
                val sample2 = sin(phase2) * 16000
                val totalSample = (sample1 + sample2).toInt().coerceIn(-32767, 32767)
                buffer[i] = totalSample.toShort()
            }
            track.write(buffer, 0, bufferSize)
        }
    }

    fun stop() {
        synthesisJob?.cancel()
        synthesisJob = null
        cleanUpTrack()
    }

    private fun cleanUpTrack() {
        try {
            audioTrack?.let { track ->
                if (track.state != AudioTrack.STATE_UNINITIALIZED) {
                    track.stop()
                    track.release()
                }
            }
        } catch (e: Exception) {
            // Ignore track release issues
        } finally {
            audioTrack = null
        }
    }
}
