package de.goldoust.eschatontools.ui.ultrasonic

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.goldoust.eschatontools.R
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.HazardYellow
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class UltrasonicTab {
    ULTRASONIC_EMITTER,
    SEISMIC_METRONOME
}

@Composable
fun UltrasonicScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(UltrasonicTab.ULTRASONIC_EMITTER) }
    val scope = rememberCoroutineScope()

    var isUltrasonicActive by remember { mutableStateOf(false) }
    var ultrasonicFreqKhz by remember { mutableStateOf(18f) }
    var toneJob by remember { mutableStateOf<Job?>(null) }

    var isMetronomeActive by remember { mutableStateOf(false) }
    var metronomeBpm by remember { mutableStateOf(60f) }
    var metronomeJob by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            toneJob?.cancel()
            metronomeJob?.cancel()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Module Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = EmergencyRedTertiary,
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.nav_ultrasonic).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.ultrasonic_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sub-Mode Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                UltrasonicTab.ULTRASONIC_EMITTER to "ULTRASONIC (15-22kHz)",
                UltrasonicTab.SEISMIC_METRONOME to "SEISMIC PULSE"
            ).forEach { (tab, label) ->
                val isSelected = selectedTab == tab
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    label = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmergencyRedTertiary,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Ultrasonic Frequency Emitter
            if (selectedTab == UltrasonicTab.ULTRASONIC_EMITTER) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HIGH FREQUENCY EMITTER",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = EmergencyRedTertiary
                                )

                                Switch(
                                    checked = isUltrasonicActive,
                                    onCheckedChange = { active ->
                                        isUltrasonicActive = active
                                        toneJob?.cancel()
                                        if (active) {
                                            toneJob = scope.launch(Dispatchers.Default) {
                                                playToneHz((ultrasonicFreqKhz * 1000).toInt())
                                            }
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmergencyRedTertiary)
                                )
                            }

                            Text(
                                text = "FREQUENCY: ${"%.1f".format(ultrasonicFreqKhz)} kHz (${(ultrasonicFreqKhz * 1000).toInt()} Hz)",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )

                            Slider(
                                value = ultrasonicFreqKhz,
                                onValueChange = {
                                    ultrasonicFreqKhz = it
                                    if (isUltrasonicActive) {
                                        toneJob?.cancel()
                                        toneJob = scope.launch(Dispatchers.Default) {
                                            playToneHz((it * 1000).toInt())
                                        }
                                    }
                                },
                                valueRange = 15f..22f,
                                colors = SliderDefaults.colors(thumbColor = EmergencyRedTertiary, activeTrackColor = EmergencyRedTertiary)
                            )

                            Text(
                                text = "Applications: Animal/wildlife deterrent, high-frequency acoustic beacon, emergency signaling.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Seismic Knocking Metronome
            if (selectedTab == UltrasonicTab.SEISMIC_METRONOME) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "SEISMIC STRUCTURAL TAPPING PULSE",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )

                            Text(
                                text = "PULSE TEMPO: ${metronomeBpm.toInt()} BPM",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )

                            Slider(
                                value = metronomeBpm,
                                onValueChange = { metronomeBpm = it },
                                valueRange = 30f..180f,
                                colors = SliderDefaults.colors(thumbColor = TacticalAmberPrimary, activeTrackColor = TacticalAmberPrimary)
                            )

                            Button(
                                onClick = {
                                    isMetronomeActive = !isMetronomeActive
                                    metronomeJob?.cancel()
                                    if (isMetronomeActive) {
                                        metronomeJob = scope.launch(Dispatchers.Default) {
                                            val intervalMs = (60_000 / metronomeBpm).toLong()
                                            while (isActive) {
                                                playToneHz(800, durationMs = 100)
                                                kotlinx.coroutines.delay(intervalMs)
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMetronomeActive) EmergencyRedTertiary else TacticalAmberPrimary,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMetronomeActive) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                                    contentDescription = null
                                )
                                Text(
                                    text = if (isMetronomeActive) "STOP SEISMIC PULSE" else "START SEISMIC PULSE",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun playToneHz(freqHz: Int, durationMs: Int = 1000) {
    try {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val sample = DoubleArray(numSamples)
        val generatedSnd = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            sample[i] = sin(2.0 * Math.PI * i / (sampleRate / freqHz.toDouble()))
        }

        var idx = 0
        for (dVal in sample) {
            val shortVal = (dVal * 32767).toInt().toShort()
            generatedSnd[idx++] = (shortVal.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = (shortVal.toInt() and 0xff00 ushr 8).toByte()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(generatedSnd.size)
            .build()

        audioTrack.write(generatedSnd, 0, generatedSnd.size)
        audioTrack.play()
    } catch (_: Exception) {}
}

@Preview(showBackground = true)
@Composable
fun UltrasonicScreenPreview() {
    EschatonToolsTheme {
        UltrasonicScreen()
    }
}
