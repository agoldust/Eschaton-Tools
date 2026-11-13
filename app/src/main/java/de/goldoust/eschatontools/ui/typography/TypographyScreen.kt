package de.goldoust.eschatontools.ui.typography

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary

enum class TypographyTab {
    NATO_PHONETIC,
    GROUND_TO_AIR,
    FIELD_CIPHER
}

@Composable
fun TypographyScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(TypographyTab.NATO_PHONETIC) }

    val natoAlphabet = remember {
        listOf(
            "A" to "Alfa", "B" to "Bravo", "C" to "Charlie", "D" to "Delta",
            "E" to "Echo", "F" to "Foxtrot", "G" to "Golf", "H" to "Hotel",
            "I" to "India", "J" to "Juliett", "K" to "Kilo", "L" to "Lima",
            "M" to "Mike", "N" to "November", "O" to "Oscar", "P" to "Papa",
            "Q" to "Quebec", "R" to "Romeo", "S" to "Sierra", "T" to "Tango",
            "U" to "Uniform", "V" to "Victor", "W" to "Whiskey", "X" to "X-ray",
            "Y" to "Yankee", "Z" to "Zulu"
        )
    }

    val sarSymbols = remember {
        listOf(
            "V" to "Require Assistance (پزشکی / عمومی)",
            "X" to "Require Medical Aid (اورژانس پزشکی)",
            "N" to "No / Negative (منفی)",
            "Y" to "Yes / Affirmative (مثبت)",
            "↑" to "Proceeding in this Direction (حرکت به این سمت)",
            "L" to "Require Fuel & Oil (سوخت)",
            "LL" to "All is Well (وضعیت خوب است)"
        )
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
                    imageVector = Icons.Rounded.FontDownload,
                    contentDescription = null,
                    tint = TacticalAmberPrimary,
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.nav_typography).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.typography_desc),
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
                TypographyTab.NATO_PHONETIC to "NATO CODE",
                TypographyTab.GROUND_TO_AIR to "AIR SIGNALS",
                TypographyTab.FIELD_CIPHER to "FIELD CIPHER"
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
            // NATO Phonetic Alphabet Table
            if (selectedTab == TypographyTab.NATO_PHONETIC) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(natoAlphabet) { (letter, code) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = letter,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalAmberPrimary
                                )
                                Text(
                                    text = code.uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Ground-to-Air Rescue Symbols Chart
            if (selectedTab == TypographyTab.GROUND_TO_AIR) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sarSymbols) { (symbol, meaning) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    color = EmergencyRedTertiary,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = symbol,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "SYMBOL: $symbol",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = TacticalAmberPrimary
                                    )
                                    Text(
                                        text = meaning,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Field Caesar Cipher Encoder / Decoder
            if (selectedTab == TypographyTab.FIELD_CIPHER) {
                var rawText by remember { mutableStateOf("") }
                var shiftKey by remember { mutableStateOf(3) }

                val encryptedText = remember(rawText, shiftKey) {
                    rawText.map { char ->
                        if (char.isLetter()) {
                            val base = if (char.isUpperCase()) 'A' else 'a'
                            ((char - base + shiftKey) % 26 + base.code).toChar()
                        } else char
                    }.joinToString("")
                }

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
                                text = "CAESAR FIELD CIPHER (OFFLINE)",
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )

                            OutlinedTextField(
                                value = rawText,
                                onValueChange = { rawText = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Input Message") },
                                placeholder = { Text("Type field note...") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TacticalAmberPrimary
                                )
                            )

                            Text(
                                text = "ENCRYPTED OUTPUT:",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (encryptedText.isBlank()) "---" else encryptedText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = TacticalGreenSecondary,
                                    modifier = Modifier.padding(12.dp)
                                )
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
fun TypographyScreenPreview() {
    EschatonToolsTheme {
        TypographyScreen()
    }
}
