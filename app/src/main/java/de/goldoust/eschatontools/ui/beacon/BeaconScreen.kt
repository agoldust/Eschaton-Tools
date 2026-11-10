package de.goldoust.eschatontools.ui.beacon

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.BatteryStd
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import de.goldoust.eschatontools.domain.beacon.SirenMode
import de.goldoust.eschatontools.domain.beacon.TorchMode
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.HazardYellow
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary
import kotlinx.coroutines.delay

@Composable
fun BeaconScreen(
    modifier: Modifier = Modifier,
    viewModel: BeaconViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isScreenBeaconOn) {
        ScreenBeaconOverlay(
            colorMode = state.screenStrobeColor,
            strobeSpeedMs = state.screenStrobeSpeedMs,
            onDismiss = { viewModel.toggleScreenBeacon(false) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module Header with Master Off button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = EmergencyRedTertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = "EMERGENCY BEACON & SOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hardware Strobe, Siren & Optical Transmitter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (viewModel.isAnyBeaconActive()) {
                    IconButton(
                        onClick = { viewModel.stopAllBeacons() },
                        modifier = Modifier.background(EmergencyRedTertiary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = "Stop All",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        // Low Battery / Safety Notice if active
        if (state.isLowBatteryWarning) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = EmergencyRedTertiary.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BatteryAlert,
                        contentDescription = null,
                        tint = EmergencyRedTertiary
                    )
                    Text(
                        text = "CRITICAL BATTERY (${state.currentBatteryPercent}%). Auto-shutoff enabled to preserve reserve power.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmergencyRedTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Primary SOS Optical Flasher Button
        val isSosActive = state.torchMode == TorchMode.SOS_STROBE
        Button(
            onClick = { viewModel.setTorchMode(TorchMode.SOS_STROBE) },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSosActive) HazardYellow else EmergencyRedTertiary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FlashOn,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = if (isSosActive) "SOS MORSE ACTIVE (... --- ...)" else "ACTIVATE MORSE SOS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (isSosActive) "Pulsing Camera Torch Flash" else "Optical Emergency Flash Pattern",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // LED Flashlight & Strobe Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "FLASHLIGHT & STROBE CONTROLLER",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TacticalAmberPrimary
                )

                // Constant Torch Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FlashOn,
                            contentDescription = null,
                            tint = TacticalAmberPrimary
                        )
                        Column {
                            Text(
                                text = "Torch Light (Constant ON)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (state.hasCameraFlash) "High efficiency LED flashlight" else "No hardware flash unit detected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = state.torchMode == TorchMode.TORCH_ON,
                        onCheckedChange = { viewModel.setTorchMode(TorchMode.TORCH_ON) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TacticalAmberPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                // High Frequency Strobe Switch & Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Speed,
                            contentDescription = null,
                            tint = TacticalAmberPrimary
                        )
                        Column {
                            Text(
                                text = "High-Frequency Strobe (${state.strobeFrequencyHz} Hz)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Rapid disorienting / signaling burst",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = state.torchMode == TorchMode.HIGH_FREQ_STROBE,
                        onCheckedChange = { viewModel.setTorchMode(TorchMode.HIGH_FREQ_STROBE) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TacticalAmberPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                if (state.torchMode == TorchMode.HIGH_FREQ_STROBE) {
                    Column {
                        Text(
                            text = "Strobe Frequency: ${state.strobeFrequencyHz} Hz",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = TacticalAmberPrimary
                        )
                        Slider(
                            value = state.strobeFrequencyHz.toFloat(),
                            onValueChange = { viewModel.setStrobeFrequency(it.toInt()) },
                            valueRange = 1f..20f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = TacticalAmberPrimary,
                                activeTrackColor = TacticalAmberPrimary
                            )
                        )
                    }
                }

                // Custom Text-to-Morse Code Transmitter
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "CUSTOM MORSE CODE TRANSMITTER",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = state.morseText,
                        onValueChange = { viewModel.setMorseText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Transmission Message") },
                        placeholder = { Text("e.g., RESCUE / SOS") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalAmberPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text(
                        text = "MORSE: ${state.morseEncodedString}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TacticalGreenSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Speed: ${state.morseWpm} WPM",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.setTorchMode(TorchMode.CUSTOM_MORSE) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.torchMode == TorchMode.CUSTOM_MORSE) HazardYellow else TacticalAmberPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (state.torchMode == TorchMode.CUSTOM_MORSE) "TRANSMITTING..." else "TRANSMIT MORSE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Acoustic Rescue Siren Synthesizer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = null,
                        tint = EmergencyRedTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "HIGH-DECIBEL AUDIO SIREN SYNTHESIZER",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmergencyRedTertiary
                    )
                }

                Text(
                    text = "Generates high intensity distress acoustic sweeps via PCM buffer synthesizer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Siren Mode Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.sirenMode == SirenMode.WAIL,
                        onClick = { viewModel.setSirenMode(SirenMode.WAIL) },
                        label = { Text("WAIL SIREN", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = { Icon(Icons.Rounded.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmergencyRedTertiary,
                            selectedLabelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = state.sirenMode == SirenMode.HI_LO,
                        onClick = { viewModel.setSirenMode(SirenMode.HI_LO) },
                        label = { Text("HI-LO POLICE", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmergencyRedTertiary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.sirenMode == SirenMode.AUDIO_MORSE_SOS,
                        onClick = { viewModel.setSirenMode(SirenMode.AUDIO_MORSE_SOS) },
                        label = { Text("AUDIO MORSE SOS", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmergencyRedTertiary,
                            selectedLabelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = state.sirenMode == SirenMode.EMERGENCY_HORN,
                        onClick = { viewModel.setSirenMode(SirenMode.EMERGENCY_HORN) },
                        label = { Text("HORN BLAST", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmergencyRedTertiary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }

                // Siren Volume Slider
                Column {
                    Text(
                        text = "Siren Output Volume: ${(state.sirenVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = state.sirenVolume,
                        onValueChange = { viewModel.setSirenVolume(it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = EmergencyRedTertiary,
                            activeTrackColor = EmergencyRedTertiary
                        )
                    )
                }
            }
        }

        // Screen Beacon Strobe Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = HazardYellow
                        )
                        Column {
                            Text(
                                text = "Screen Strobe Beacon",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Full-screen high contrast visual signaling",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = state.isScreenBeaconOn,
                        onCheckedChange = { viewModel.toggleScreenBeacon(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = HazardYellow,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.screenStrobeColor == ScreenBeaconColor.WHITE_RED_STROBE,
                        onClick = { viewModel.setScreenStrobeColor(ScreenBeaconColor.WHITE_RED_STROBE) },
                        label = { Text("WHITE / RED", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = state.screenStrobeColor == ScreenBeaconColor.WHITE_BLACK_STROBE,
                        onClick = { viewModel.setScreenStrobeColor(ScreenBeaconColor.WHITE_BLACK_STROBE) },
                        label = { Text("WHITE / BLACK", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = state.screenStrobeColor == ScreenBeaconColor.HAZARD_YELLOW_STROBE,
                        onClick = { viewModel.setScreenStrobeColor(ScreenBeaconColor.HAZARD_YELLOW_STROBE) },
                        label = { Text("HAZARD YELLOW", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Safety Controls & Auto-Shutoff Timer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = TacticalAmberPrimary
                    )
                    Text(
                        text = "SAFETY TOGGLES & BATTERY GUARD",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TacticalAmberPrimary
                    )
                }

                // Battery Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BatteryStd,
                            contentDescription = null,
                            tint = TacticalGreenSecondary
                        )
                        Text(
                            text = "Battery Reserve: ${state.currentBatteryPercent}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "15% Auto-Guard",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = state.batteryAutoShutoffEnabled,
                            onCheckedChange = { viewModel.setBatteryAutoShutoff(it) }
                        )
                    }
                }

                // Auto-Shutoff Timer Chips
                Text(
                    text = "Auto-Shutoff Timer:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0 to "OFF", 5 to "5 MIN", 15 to "15 MIN", 30 to "30 MIN", 60 to "60 MIN").forEach { (min, label) ->
                        FilterChip(
                            selected = state.autoShutoffMinutes == min,
                            onClick = { viewModel.setAutoShutoffMinutes(min) },
                            label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                if (state.autoShutoffRemainingSeconds != null) {
                    val remaining = state.autoShutoffRemainingSeconds ?: 0
                    val minutesLeft = remaining / 60
                    val secondsLeft = remaining % 60
                    val formatted = "%02d:%02d".format(minutesLeft, secondsLeft)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = null,
                            tint = HazardYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AUTO SHUTOFF IN: $formatted",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = HazardYellow
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen high-contrast strobing display for visual signaling.
 */
@Composable
fun ScreenBeaconOverlay(
    colorMode: ScreenBeaconColor,
    strobeSpeedMs: Long,
    onDismiss: () -> Unit
) {
    var isPhaseOne by remember { mutableStateOf(true) }

    LaunchedEffect(strobeSpeedMs) {
        while (true) {
            delay(strobeSpeedMs)
            isPhaseOne = !isPhaseOne
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = when (colorMode) {
            ScreenBeaconColor.WHITE_RED_STROBE -> if (isPhaseOne) Color.White else EmergencyRedTertiary
            ScreenBeaconColor.WHITE_BLACK_STROBE -> if (isPhaseOne) Color.White else Color.Black
            ScreenBeaconColor.HAZARD_YELLOW_STROBE -> if (isPhaseOne) HazardYellow else Color.Black
            ScreenBeaconColor.SOLID_WHITE -> Color.White
        },
        label = "ScreenStrobeColor"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White
                    )
                    Text(
                        text = "TAP SCREEN TO EXIT BEACON",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BeaconScreenPreview() {
    EschatonToolsTheme {
        BeaconScreen()
    }
}
