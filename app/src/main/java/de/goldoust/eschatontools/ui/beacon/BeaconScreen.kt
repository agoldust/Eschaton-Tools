package de.goldoust.eschatontools.ui.beacon

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import de.goldoust.eschatontools.R
import de.goldoust.eschatontools.domain.beacon.SirenMode
import de.goldoust.eschatontools.domain.beacon.TorchMode
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.HazardYellow
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary
import kotlinx.coroutines.delay

enum class BeaconTab(
    val titleRes: Int,
    val icon: ImageVector
) {
    FLASH(R.string.strobe_flasher, Icons.Rounded.FlashOn),
    SIREN(R.string.audio_siren_generator, Icons.AutoMirrored.Rounded.VolumeUp),
    MORSE(R.string.custom_morse_transmitter, Icons.Rounded.GraphicEq),
    SCREEN(R.string.screen_sos_beacon, Icons.Rounded.Lightbulb)
}

@Composable
fun BeaconScreen(
    modifier: Modifier = Modifier,
    viewModel: BeaconViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(BeaconTab.FLASH) }

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
        // Big Icon Sub-Section Mode Selector Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BeaconTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    onClick = { selectedTab = tab },
                    modifier = Modifier.weight(1f),
                    color = if (isSelected) EmergencyRedTertiary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) EmergencyRedTertiary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.titleRes),
                            modifier = Modifier.size(26.dp),
                            tint = if (isSelected) Color.Black else EmergencyRedTertiary
                        )
                        Text(
                            text = stringResource(tab.titleRes),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Master Stop All & Low Battery Header Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BatteryStd,
                        contentDescription = null,
                        tint = TacticalGreenSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "BATTERY: ${state.currentBatteryPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (viewModel.isAnyBeaconActive()) {
                    Button(
                        onClick = { viewModel.stopAllBeacons() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmergencyRedTertiary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PowerSettingsNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.stop_all).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Low Battery Notice
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
                        text = stringResource(R.string.critical_battery_warning, state.currentBatteryPercent),
                        style = MaterialTheme.typography.bodySmall,
                        color = EmergencyRedTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sub-Section 1: Optical Flasher & Strobe Controls
        AnimatedVisibility(visible = selectedTab == BeaconTab.FLASH) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Primary SOS Optical Flasher Button
                val isSosActive = state.torchMode == TorchMode.SOS_STROBE
                Button(
                    onClick = { viewModel.setTorchMode(TorchMode.SOS_STROBE) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
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
                                text = if (isSosActive) stringResource(R.string.sos_morse_active) else stringResource(R.string.activate_morse_sos),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (isSosActive) stringResource(R.string.pulsing_camera_flash) else stringResource(R.string.optical_flash_pattern),
                                fontSize = 11.sp,
                                color = Color.Black.copy(alpha = 0.85f)
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
                            text = stringResource(R.string.flashlight_strobe_controller),
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
                                        text = stringResource(R.string.torch_light),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.constant_led_flashlight),
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
                                        text = stringResource(R.string.strobe_flasher),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.high_freq_strobe),
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
                                    text = stringResource(R.string.strobe_frequency, state.strobeFrequencyHz),
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
                    }
                }
            }
        }

        // Sub-Section 2: Acoustic Rescue Siren Synthesizer
        AnimatedVisibility(visible = selectedTab == BeaconTab.SIREN) {
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
                            text = stringResource(R.string.audio_siren_generator),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRedTertiary
                        )
                    }

                    Text(
                        text = stringResource(R.string.high_decibel_siren),
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
                            label = { Text("WAIL", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Rounded.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmergencyRedTertiary,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = state.sirenMode == SirenMode.HI_LO,
                            onClick = { viewModel.setSirenMode(SirenMode.HI_LO) },
                            label = { Text("HI-LO", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
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
                            label = { Text("AUDIO MORSE", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmergencyRedTertiary,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = state.sirenMode == SirenMode.EMERGENCY_HORN,
                            onClick = { viewModel.setSirenMode(SirenMode.EMERGENCY_HORN) },
                            label = { Text("HORN", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmergencyRedTertiary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }

                    // Siren Volume Slider
                    Column {
                        Text(
                            text = stringResource(R.string.siren_volume, (state.sirenVolume * 100).toInt()),
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
        }

        // Sub-Section 3: Custom Text-to-Morse Transmitter
        AnimatedVisibility(visible = selectedTab == BeaconTab.MORSE) {
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
                    Text(
                        text = stringResource(R.string.custom_morse_transmitter),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = state.morseText,
                        onValueChange = { viewModel.setMorseText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.text_to_transmit)) },
                        placeholder = { Text("e.g. HELP ME / SOS") },
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
                                text = if (state.torchMode == TorchMode.CUSTOM_MORSE) stringResource(R.string.morse_transmitting) else stringResource(R.string.transmit_morse_loop),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Sub-Section 4: Screen Beacon Strobe
        AnimatedVisibility(visible = selectedTab == BeaconTab.SCREEN) {
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
                                    text = stringResource(R.string.screen_sos_beacon),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.full_screen_flasher),
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
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(R.string.tap_to_dismiss),
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
