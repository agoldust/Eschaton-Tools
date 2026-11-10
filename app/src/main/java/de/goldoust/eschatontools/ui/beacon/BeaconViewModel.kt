package de.goldoust.eschatontools.ui.beacon

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.goldoust.eschatontools.domain.beacon.AudioSirenSynthesizer
import de.goldoust.eschatontools.domain.beacon.FlashlightController
import de.goldoust.eschatontools.domain.beacon.MorseCode
import de.goldoust.eschatontools.domain.beacon.SirenMode
import de.goldoust.eschatontools.domain.beacon.TorchMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class ScreenBeaconColor {
    WHITE_RED_STROBE,
    WHITE_BLACK_STROBE,
    HAZARD_YELLOW_STROBE,
    SOLID_WHITE
}

data class BeaconUiState(
    val isTorchOn: Boolean = false,
    val torchMode: TorchMode = TorchMode.OFF,
    val strobeFrequencyHz: Int = 10,
    val morseText: String = "SOS",
    val morseWpm: Int = 15,
    val morseEncodedString: String = "... --- ...",
    val isAudioSirenOn: Boolean = false,
    val sirenMode: SirenMode = SirenMode.OFF,
    val sirenVolume: Float = 0.8f,
    val isScreenBeaconOn: Boolean = false,
    val screenStrobeColor: ScreenBeaconColor = ScreenBeaconColor.WHITE_RED_STROBE,
    val screenStrobeSpeedMs: Long = 250L,
    val autoShutoffMinutes: Int = 0,
    val autoShutoffRemainingSeconds: Int? = null,
    val batteryAutoShutoffEnabled: Boolean = true,
    val batteryThresholdPercent: Int = 15,
    val currentBatteryPercent: Int = 85,
    val isLowBatteryWarning: Boolean = false,
    val hasCameraFlash: Boolean = true
)

class BeaconViewModel(application: Application) : AndroidViewModel(application) {

    private val flashlightController = FlashlightController(application)
    private val audioSirenSynthesizer = AudioSirenSynthesizer()

    private val _uiState = MutableStateFlow(BeaconUiState())
    val uiState: StateFlow<BeaconUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var batteryReceiver: BroadcastReceiver? = null

    init {
        val hasFlash = flashlightController.hasFlashUnit()
        _uiState.update {
            it.copy(
                hasCameraFlash = hasFlash,
                morseEncodedString = MorseCode.encodeToMorse(it.morseText)
            )
        }
        registerBatteryReceiver()
    }

    private fun registerBatteryReceiver() {
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = getApplication<Application>().registerReceiver(null, filter)
            updateBatteryStatus(batteryStatus)

            batteryReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    updateBatteryStatus(intent)
                }
            }
            getApplication<Application>().registerReceiver(batteryReceiver, filter)
        } catch (e: Exception) {
            // Default battery fallback
        }
    }

    private fun updateBatteryStatus(intent: Intent?) {
        if (intent == null) return
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level >= 0 && scale > 0) {
            val pct = ((level / scale.toFloat()) * 100).toInt()
            val state = _uiState.value
            val isLow = pct <= state.batteryThresholdPercent

            _uiState.update {
                it.copy(
                    currentBatteryPercent = pct,
                    isLowBatteryWarning = isLow
                )
            }

            if (isLow && state.batteryAutoShutoffEnabled && isAnyBeaconActive()) {
                stopAllBeacons()
            }
        }
    }

    fun isAnyBeaconActive(): Boolean {
        val s = _uiState.value
        return s.torchMode != TorchMode.OFF || s.sirenMode != SirenMode.OFF || s.isScreenBeaconOn
    }

    fun setTorchMode(mode: TorchMode) {
        val currentState = _uiState.value
        val newMode = if (currentState.torchMode == mode) TorchMode.OFF else mode

        _uiState.update { it.copy(torchMode = newMode, isTorchOn = newMode != TorchMode.OFF) }

        flashlightController.setTorchMode(
            mode = newMode,
            strobeFreqHz = currentState.strobeFrequencyHz,
            morseText = currentState.morseText,
            morseWpm = currentState.morseWpm
        )

        onBeaconStateChanged()
    }

    fun setStrobeFrequency(freqHz: Int) {
        val clamped = freqHz.coerceIn(1, 20)
        _uiState.update { it.copy(strobeFrequencyHz = clamped) }

        if (_uiState.value.torchMode == TorchMode.HIGH_FREQ_STROBE) {
            flashlightController.setTorchMode(
                mode = TorchMode.HIGH_FREQ_STROBE,
                strobeFreqHz = clamped
            )
        }
    }

    fun setMorseText(text: String) {
        val encoded = MorseCode.encodeToMorse(text)
        _uiState.update {
            it.copy(
                morseText = text,
                morseEncodedString = encoded
            )
        }

        val mode = _uiState.value.torchMode
        if (mode == TorchMode.CUSTOM_MORSE || mode == TorchMode.SOS_STROBE) {
            flashlightController.setTorchMode(
                mode = mode,
                morseText = text,
                morseWpm = _uiState.value.morseWpm
            )
        }
    }

    fun setMorseWpm(wpm: Int) {
        val clamped = wpm.coerceIn(5, 30)
        _uiState.update { it.copy(morseWpm = clamped) }

        val mode = _uiState.value.torchMode
        if (mode == TorchMode.CUSTOM_MORSE || mode == TorchMode.SOS_STROBE) {
            flashlightController.setTorchMode(
                mode = mode,
                morseText = _uiState.value.morseText,
                morseWpm = clamped
            )
        }
    }

    fun setSirenMode(mode: SirenMode) {
        val currentState = _uiState.value
        val newMode = if (currentState.sirenMode == mode) SirenMode.OFF else mode

        _uiState.update {
            it.copy(
                sirenMode = newMode,
                isAudioSirenOn = newMode != SirenMode.OFF
            )
        }

        audioSirenSynthesizer.playSiren(
            mode = newMode,
            volume = currentState.sirenVolume,
            morseWpm = currentState.morseWpm
        )

        onBeaconStateChanged()
    }

    fun setSirenVolume(volume: Float) {
        val clamped = volume.coerceIn(0.0f, 1.0f)
        _uiState.update { it.copy(sirenVolume = clamped) }
        audioSirenSynthesizer.setVolume(clamped)
    }

    fun toggleScreenBeacon(enabled: Boolean? = null) {
        val newStatus = enabled ?: !_uiState.value.isScreenBeaconOn
        _uiState.update { it.copy(isScreenBeaconOn = newStatus) }
        onBeaconStateChanged()
    }

    fun setScreenStrobeColor(color: ScreenBeaconColor) {
        _uiState.update { it.copy(screenStrobeColor = color) }
    }

    fun setScreenStrobeSpeed(speedMs: Long) {
        val clamped = speedMs.coerceIn(50L, 1000L)
        _uiState.update { it.copy(screenStrobeSpeedMs = clamped) }
    }

    fun setAutoShutoffMinutes(minutes: Int) {
        val safeMinutes = minutes.coerceIn(0, 60)
        _uiState.update { it.copy(autoShutoffMinutes = safeMinutes) }
        onBeaconStateChanged()
    }

    fun setBatteryAutoShutoff(enabled: Boolean, thresholdPercent: Int = 15) {
        _uiState.update {
            it.copy(
                batteryAutoShutoffEnabled = enabled,
                batteryThresholdPercent = thresholdPercent.coerceIn(5, 50)
            )
        }
    }

    private fun onBeaconStateChanged() {
        if (isAnyBeaconActive() && _uiState.value.autoShutoffMinutes > 0) {
            startAutoShutoffTimer(_uiState.value.autoShutoffMinutes)
        } else if (!isAnyBeaconActive()) {
            stopAutoShutoffTimer()
        }
    }

    private fun startAutoShutoffTimer(minutes: Int) {
        stopAutoShutoffTimer()
        val totalSeconds = minutes * 60
        _uiState.update { it.copy(autoShutoffRemainingSeconds = totalSeconds) }

        timerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (isActive && remaining > 0) {
                delay(1000L)
                remaining--
                _uiState.update { it.copy(autoShutoffRemainingSeconds = remaining) }
            }
            if (remaining <= 0) {
                stopAllBeacons()
            }
        }
    }

    private fun stopAutoShutoffTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(autoShutoffRemainingSeconds = null) }
    }

    fun stopAllBeacons() {
        flashlightController.stop()
        audioSirenSynthesizer.stop()
        _uiState.update {
            it.copy(
                torchMode = TorchMode.OFF,
                isTorchOn = false,
                sirenMode = SirenMode.OFF,
                isAudioSirenOn = false,
                isScreenBeaconOn = false
            )
        }
        stopAutoShutoffTimer()
    }

    override fun onCleared() {
        super.onCleared()
        stopAllBeacons()
        batteryReceiver?.let {
            try {
                getApplication<Application>().unregisterReceiver(it)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
