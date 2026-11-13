package de.goldoust.eschatontools.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

enum class BudgetTab {
    RATION_CALCULATOR,
    POWER_BUDGET
}

@Composable
fun RationBudgetScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BudgetTab.RATION_CALCULATOR) }

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
                    imageVector = Icons.Rounded.Calculate,
                    contentDescription = null,
                    tint = TacticalAmberPrimary,
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.nav_budget).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.budget_desc),
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
                BudgetTab.RATION_CALCULATOR to "FOOD & WATER",
                BudgetTab.POWER_BUDGET to "BATTERY RUNTIME"
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
                        selectedContainerColor = TacticalAmberPrimary,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Food & Water Ration Calculator
            if (selectedTab == BudgetTab.RATION_CALCULATOR) {
                var survivorsInput by remember { mutableStateOf("2") }
                var waterLitersInput by remember { mutableStateOf("20") }
                var caloriesKcalInput by remember { mutableStateOf("15000") }

                val count = survivorsInput.toIntOrNull() ?: 1
                val water = waterLitersInput.toDoubleOrNull() ?: 0.0
                val calories = caloriesKcalInput.toDoubleOrNull() ?: 0.0

                // Standard survival minimums: 2.5 Liters/person/day, 1800 kcal/person/day
                val waterDays = if (count > 0) water / (count * 2.5) else 0.0
                val foodDays = if (count > 0) calories / (count * 1800.0) else 0.0

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
                                text = "TEAM SURVIVAL RATION CALCULATOR",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )

                            OutlinedTextField(
                                value = survivorsInput,
                                onValueChange = { survivorsInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Number of Survivors") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TacticalAmberPrimary)
                            )

                            OutlinedTextField(
                                value = waterLitersInput,
                                onValueChange = { waterLitersInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Total Available Water (Liters)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TacticalAmberPrimary)
                            )

                            OutlinedTextField(
                                value = caloriesKcalInput,
                                onValueChange = { caloriesKcalInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Total Food Rations (Kcal)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TacticalAmberPrimary)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "WATER DAYS LEFT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "%.1f Days".format(waterDays),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalGreenSecondary
                                    )
                                }

                                Column {
                                    Text(
                                        text = "FOOD DAYS LEFT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "%.1f Days".format(foodDays),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalAmberPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Powerbank & Device Battery Runtime Calculator
            if (selectedTab == BudgetTab.POWER_BUDGET) {
                var batteryMahInput by remember { mutableStateOf("10000") }
                val mahVal = batteryMahInput.toIntOrNull() ?: 10000

                // Estimated draw: Idle=150mA, GPS=300mA, Strobe=600mA, Mesh=350mA
                val hoursIdle = mahVal / 150.0
                val hoursGps = mahVal / 300.0
                val hoursStrobe = mahVal / 600.0

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
                                text = "POWERBANK RUNTIME ESTIMATOR",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )

                            OutlinedTextField(
                                value = batteryMahInput,
                                onValueChange = { batteryMahInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Battery Capacity (mAh)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TacticalAmberPrimary)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "STANDBY",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${hoursIdle.toInt()} Hours",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalGreenSecondary
                                    )
                                }

                                Column {
                                    Text(
                                        text = "GPS COMPASS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${hoursGps.toInt()} Hours",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalAmberPrimary
                                    )
                                }

                                Column {
                                    Text(
                                        text = "LED STROBE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${hoursStrobe.toInt()} Hours",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = EmergencyRedTertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RationBudgetScreenPreview() {
    EschatonToolsTheme {
        RationBudgetScreen()
    }
}
