package de.goldoust.eschatontools.ui.compass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.AddLocation
import androidx.compose.material.icons.rounded.CompassCalibration
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.GpsNotFixed
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.goldoust.eschatontools.domain.Waypoint
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.HazardYellow
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary
import de.goldoust.eschatontools.ui.theme.TacticalGridLine
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassScreen(
    modifier: Modifier = Modifier,
    compassViewModel: CompassViewModel = viewModel()
) {
    val uiState by compassViewModel.uiState.collectAsState()
    var showAddWaypointDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Module Header Card with True North Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Explore,
                        contentDescription = null,
                        tint = TacticalAmberPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "SOLAR COMPASS & NAV",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.latitude != null) Icons.Rounded.GpsFixed else Icons.Rounded.GpsNotFixed,
                                contentDescription = null,
                                tint = if (uiState.latitude != null) TacticalGreenSecondary else HazardYellow,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.latitude != null) "GPS LOCKED" else "ACQUIRING GPS...",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = if (uiState.latitude != null) TacticalGreenSecondary else HazardYellow
                            )
                        }
                    }
                }

                FilterChip(
                    selected = uiState.isTrueNorth,
                    onClick = { compassViewModel.toggleTrueNorth() },
                    label = {
                        Text(
                            text = if (uiState.isTrueNorth) "TRUE N" else "MAG N",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TacticalAmberPrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        // Live Compass Ring & Dial Graphic
        Box(
            modifier = Modifier
                .size(270.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(2.dp, TacticalAmberPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Dial Hash Marks & Cardinal Indicators
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2 - 12.dp.toPx()

                // Outer border grid
                drawCircle(
                    color = TacticalGridLine,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                // 360 degree hash marks
                val degreesStep = 15
                for (angle in 0 until 360 step degreesStep) {
                    val angleRad = Math.toRadians((angle - 90).toDouble())
                    val isMajor = angle % 90 == 0
                    val isMedium = angle % 45 == 0
                    val tickLen = if (isMajor) 14.dp.toPx() else if (isMedium) 10.dp.toPx() else 6.dp.toPx()

                    val startX = (center.x + (radius - tickLen) * cos(angleRad)).toFloat()
                    val startY = (center.y + (radius - tickLen) * sin(angleRad)).toFloat()
                    val endX = (center.x + radius * cos(angleRad)).toFloat()
                    val endY = (center.y + radius * sin(angleRad)).toFloat()

                    drawLine(
                        color = if (isMajor) TacticalAmberPrimary else TacticalGridLine,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                    )
                }
            }

            // Sun position indicator on dial ring (if Daytime & sunAzimuth available)
            uiState.sunAzimuth?.let { sunAz ->
                val relativeSunAngle = (sunAz - uiState.displayAzimuth + 360f) % 360f
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(relativeSunAngle),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WbSunny,
                        contentDescription = "Sun Direction",
                        tint = HazardYellow,
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .size(20.dp)
                    )
                }
            }

            // Target Waypoint position indicator on dial ring (if active waypoint exists)
            uiState.bearingToActiveWaypoint?.let { bearing ->
                val relativeWaypointAngle = (bearing - uiState.displayAzimuth + 360f) % 360f
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(relativeWaypointAngle),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Navigation,
                        contentDescription = "Waypoint Direction",
                        tint = TacticalGreenSecondary,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(22.dp)
                    )
                }
            }

            // Rotating Needle Pointer (Smooth Low-Pass Filtered)
            Canvas(
                modifier = Modifier
                    .size(210.dp)
                    .rotate(-uiState.displayAzimuth)
            ) {
                val width = size.width
                val height = size.height
                val center = Offset(width / 2, height / 2)

                // North Needle (Red/Amber)
                val northPath = Path().apply {
                    moveTo(center.x, center.y - (height / 2.2f))
                    lineTo(center.x - 14.dp.toPx(), center.y)
                    lineTo(center.x + 14.dp.toPx(), center.y)
                    close()
                }
                drawPath(path = northPath, color = EmergencyRedTertiary)

                // South Needle (Silver/Dark)
                val southPath = Path().apply {
                    moveTo(center.x, center.y + (height / 2.2f))
                    lineTo(center.x - 14.dp.toPx(), center.y)
                    lineTo(center.x + 14.dp.toPx(), center.y)
                    close()
                }
                drawPath(path = southPath, color = Color.Gray)
            }

            // Center Digital Readout Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape)
                    .border(1.5.dp, TacticalAmberPrimary.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.displayAzimuth.toInt()}°",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TacticalAmberPrimary
                    )
                    Text(
                        text = uiState.cardinalDirection,
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Tactical Pitch & Roll Horizon Level Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PITCH",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${if (uiState.pitch >= 0) "+" else ""}${uiState.pitch.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TacticalAmberPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ROLL",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${if (uiState.roll >= 0) "+" else ""}${uiState.roll.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TacticalAmberPrimary
                    )
                }
            }
        }

        // Live GPS Coordinates Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = HazardYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "CURRENT GPS POSITION",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = HazardYellow
                        )
                    }

                    Text(
                        text = "DECL: ${if (uiState.declination >= 0) "+" else ""}${"%.1f".format(uiState.declination)}°",
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TacticalAmberPrimary
                    )
                }

                val latStr = uiState.latitude?.let { "%.5f°".format(it) } ?: "---"
                val lonStr = uiState.longitude?.let { "%.5f°".format(it) } ?: "---"
                val altStr = uiState.altitude?.let { "${it.toInt()}m" } ?: "---"
                val accStr = uiState.accuracy?.let { "±${"%.1f".format(it)}m" } ?: "---"

                Text(
                    text = "LAT: $latStr   LON: $lonStr",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "ALTITUDE: $altStr  •  ACCURACY: $accStr",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Offline Solar Position & Daytime Calibration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CompassCalibration,
                        contentDescription = null,
                        tint = HazardYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "SOLAR CALIBRATION (OFFLINE)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = HazardYellow
                    )
                }

                if (uiState.sunAzimuth != null && uiState.sunElevation != null) {
                    val sunAzStr = "%.1f°".format(uiState.sunAzimuth)
                    val sunElStr = "${if (uiState.sunElevation!! >= 0) "+" else ""}${"%.1f".format(uiState.sunElevation)}°"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "SUN AZIMUTH",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = sunAzStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "SUN ALTITUDE",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = sunElStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalAmberPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "DAY/NIGHT",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (uiState.isDaytime) "DAYTIME" else "NIGHT",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isDaytime) TacticalGreenSecondary else EmergencyRedTertiary
                            )
                        }
                    }

                    Text(
                        text = if (uiState.isDaytime) {
                            "Daytime Calibration: Point device top edge directly toward the Sun. Your heading should align with Sun Azimuth (${sunAzStr})."
                        } else {
                            "Nighttime: Solar calibration inactive. Use magnetic sensor or Polaris star alignment."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Waiting for GPS coordinates to compute solar position...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Active Waypoint Navigation Info Card (if selected)
        uiState.activeWaypoint?.let { activeWp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NAVIGATING TO",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = TacticalGreenSecondary
                        )
                        Text(
                            text = activeWp.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        uiState.distanceToActiveWaypoint?.let { dist ->
                            val distText = if (dist >= 1000) "%.2f km".format(dist / 1000f) else "${dist.toInt()} m"
                            val bearingText = uiState.bearingToActiveWaypoint?.let { " | Bearing: ${it.toInt()}°" } ?: ""
                            Text(
                                text = "Distance: $distText$bearingText",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = TacticalAmberPrimary
                            )
                        }
                    }

                    IconButton(onClick = { compassViewModel.selectActiveWaypoint(null) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Clear Target Waypoint",
                            tint = EmergencyRedTertiary
                        )
                    }
                }
            }
        }

        // Action Buttons: Save & View Waypoints
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { showAddWaypointDialog = true },
                enabled = uiState.latitude != null,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TacticalAmberPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddLocation,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SAVE WAYPOINT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Saved Waypoints List Card (if any saved)
        if (uiState.waypoints.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SAVED OFFLINE WAYPOINTS (${uiState.waypoints.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TacticalAmberPrimary
                    )

                    uiState.waypoints.forEach { wp ->
                        val isSelected = uiState.activeWaypoint?.id == wp.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { compassViewModel.selectActiveWaypoint(wp) }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = wp.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "%.4f°, %.4f°".format(wp.latitude, wp.longitude),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = TacticalGreenSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                IconButton(
                                    onClick = { compassViewModel.deleteWaypoint(wp) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Delete Waypoint",
                                        tint = EmergencyRedTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Waypoint Dialog
    if (showAddWaypointDialog) {
        var waypointName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddWaypointDialog = false },
            title = {
                Text(
                    text = "SAVE OFFLINE WAYPOINT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TacticalAmberPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter a label for your current GPS location:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = waypointName,
                        onValueChange = { waypointName = it },
                        placeholder = { Text("e.g. Base Camp, Water Source") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalAmberPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        compassViewModel.addWaypoint(waypointName)
                        showAddWaypointDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalAmberPrimary)
                ) {
                    Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWaypointDialog = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CompassScreenPreview() {
    EschatonToolsTheme {
        CompassScreen()
    }
}
