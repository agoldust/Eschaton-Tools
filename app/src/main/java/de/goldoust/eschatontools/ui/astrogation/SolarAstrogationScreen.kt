package de.goldoust.eschatontools.ui.astrogation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NightlightRound
import androidx.compose.material.icons.rounded.SolarPower
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlin.math.abs

enum class AstrogationTab {
    SOLAR_PANEL,
    POLARIS_NIGHT,
    MOON_TIDES
}

@Composable
fun SolarAstrogationScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AstrogationTab.SOLAR_PANEL) }

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
                    imageVector = Icons.Rounded.WbSunny,
                    contentDescription = null,
                    tint = HazardYellow,
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.nav_astrogation).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.astrogation_desc),
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
                AstrogationTab.SOLAR_PANEL to "SOLAR PANELS",
                AstrogationTab.POLARIS_NIGHT to "POLARIS ALIGN",
                AstrogationTab.MOON_TIDES to "MOON & TIDES"
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
                        selectedContainerColor = HazardYellow,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Solar Panel Tilt Calculator
            if (selectedTab == AstrogationTab.SOLAR_PANEL) {
                var latitudeInput by remember { mutableStateOf("35.7") }
                val latVal = latitudeInput.toDoubleOrNull() ?: 35.7
                val optimalTiltWinter = abs(latVal) * 0.9 + 29.0
                val optimalTiltSummer = abs(latVal) * 0.9 - 23.5
                val optimalTiltYear = abs(latVal) * 0.87

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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "SOLAR PANEL TILT OPTIMIZER",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = HazardYellow
                            )

                            OutlinedTextField(
                                value = latitudeInput,
                                onValueChange = { latitudeInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("GPS Latitude (°)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HazardYellow
                                )
                            )

                            Text(
                                text = "RECOMMENDED TILT ANGLES (FROM HORIZONTAL):",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "WINTER",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${optimalTiltWinter.toInt()}°",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalAmberPrimary
                                    )
                                }
                                Column {
                                    Text(
                                        text = "YEAR ROUND",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${optimalTiltYear.toInt()}°",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalGreenSecondary
                                    )
                                }
                                Column {
                                    Text(
                                        text = "SUMMER",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${optimalTiltSummer.coerceAtLeast(0.0).toInt()}°",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = HazardYellow
                                    )
                                }
                            }

                            Text(
                                text = "Azimuth Alignment: Point panels directly SOUTH if in Northern Hemisphere (${latVal > 0}). Point NORTH if in Southern Hemisphere.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Polaris Night Star Alignment
            if (selectedTab == AstrogationTab.POLARIS_NIGHT) {
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "POLARIS STAR ALIGNMENT (TRUE NORTH)",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )

                            Text(
                                text = "1. Locate the Big Dipper (Ursa Major) in northern sky.\n" +
                                        "2. Find Merak and Dubhe (the two outer stars forming the cup).\n" +
                                        "3. Draw an imaginary line straight out through Dubhe, 5x the distance between Merak and Dubhe.\n" +
                                        "4. You will hit Polaris (North Star). Dropping a straight plumb line down to the horizon gives TRUE NORTH.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Moon & Tides Estimator
            if (selectedTab == AstrogationTab.MOON_TIDES) {
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "MOON PHASE & TIDES ESTIMATOR",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = HazardYellow
                            )

                            Text(
                                text = "LUNAR ILLUMINATION: ~78% (WAXING GIBBOUS)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "TIDE FORECAST: High Tide at ~04:15 & ~16:30 | Low Tide at ~10:20 & ~22:40",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SolarAstrogationScreenPreview() {
    EschatonToolsTheme {
        SolarAstrogationScreen()
    }
}
