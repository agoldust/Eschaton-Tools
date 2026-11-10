package de.goldoust.eschatontools.domain.beacon

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class TorchMode {
    OFF,
    TORCH_ON,
    SOS_STROBE,
    HIGH_FREQ_STROBE,
    CUSTOM_MORSE
}

class FlashlightController(
    private val context: Context
) {
    private val cameraManager: CameraManager? by lazy {
        try {
            context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        } catch (e: Exception) {
            null
        }
    }

    private var cameraId: String? = null
    private var isTorchActive = false
    private var strobeJob: Job? = null
    private val controllerScope = CoroutineScope(Dispatchers.Default)

    init {
        detectCameraWithFlash()
    }

    private fun detectCameraWithFlash() {
        try {
            val manager = cameraManager ?: return
            for (id in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(id)
                val flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (flashAvailable && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id
                    break
                } else if (flashAvailable && cameraId == null) {
                    cameraId = id
                }
            }
        } catch (e: Exception) {
            cameraId = null
        }
    }

    fun hasFlashUnit(): Boolean {
        if (cameraId == null) detectCameraWithFlash()
        return cameraId != null
    }

    fun setTorchMode(
        mode: TorchMode,
        strobeFreqHz: Int = 10,
        morseText: String = "SOS",
        morseWpm: Int = 15,
        onStateChanged: ((Boolean) -> Unit)? = null
    ) {
        stopStrobeJob()

        when (mode) {
            TorchMode.OFF -> {
                setFlashState(false)
                onStateChanged?.invoke(false)
            }
            TorchMode.TORCH_ON -> {
                setFlashState(true)
                onStateChanged?.invoke(true)
            }
            TorchMode.HIGH_FREQ_STROBE -> {
                strobeJob = controllerScope.launch {
                    val safeFreq = strobeFreqHz.coerceIn(1, 25)
                    val periodMs = 1000L / safeFreq
                    val halfPeriodMs = (periodMs / 2).coerceAtLeast(10L)

                    while (isActive) {
                        setFlashState(true)
                        onStateChanged?.invoke(true)
                        delay(halfPeriodMs)
                        setFlashState(false)
                        onStateChanged?.invoke(false)
                        delay(halfPeriodMs)
                    }
                }
            }
            TorchMode.SOS_STROBE -> {
                strobeJob = controllerScope.launch {
                    val timings = MorseCode.encodeToTimings("SOS", morseWpm)
                    runMorseLoop(timings, onStateChanged)
                }
            }
            TorchMode.CUSTOM_MORSE -> {
                strobeJob = controllerScope.launch {
                    val timings = MorseCode.encodeToTimings(morseText.ifBlank { "SOS" }, morseWpm)
                    runMorseLoop(timings, onStateChanged)
                }
            }
        }
    }

    private suspend fun runMorseLoop(
        timings: List<MorseTimingStep>,
        onStateChanged: ((Boolean) -> Unit)?
    ) {
        if (timings.isEmpty()) return
        val loopDelayMs = 2000L

        while (strobeJob?.isActive == true) {
            for (step in timings) {
                if (strobeJob?.isActive != true) break
                setFlashState(step.isOn)
                onStateChanged?.invoke(step.isOn)
                delay(step.durationMs)
            }
            // End of message delay
            setFlashState(false)
            onStateChanged?.invoke(false)
            delay(loopDelayMs)
        }
    }

    private fun setFlashState(enabled: Boolean) {
        val id = cameraId ?: return
        val manager = cameraManager ?: return
        try {
            if (isTorchActive != enabled) {
                manager.setTorchMode(id, enabled)
                isTorchActive = enabled
            }
        } catch (e: CameraAccessException) {
            isTorchActive = false
        } catch (e: Exception) {
            isTorchActive = false
        }
    }

    fun stop() {
        stopStrobeJob()
        setFlashState(false)
    }

    private fun stopStrobeJob() {
        strobeJob?.cancel()
        strobeJob = null
    }
}
