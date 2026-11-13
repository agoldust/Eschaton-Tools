package de.goldoust.eschatontools.ui.map

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.goldoust.eschatontools.R
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.HazardYellow
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary
import de.goldoust.eschatontools.ui.theme.TacticalGridLine
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TacticalMapScreen(
    modifier: Modifier = Modifier
) {
    var radarRangeMeters by remember { mutableStateOf(500) }

    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    imageVector = Icons.Rounded.Radar,
                    contentDescription = null,
                    tint = TacticalGreenSecondary,
                    modifier = Modifier.size(30.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.nav_map).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.map_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Tactical HUD Radar Display
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(2.dp, TacticalGreenSecondary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val maxRadius = size.width / 2 - 12.dp.toPx()

                // Radar Range Rings (Outer, Middle, Inner)
                val ringCount = 4
                for (i in 1..ringCount) {
                    val radius = (maxRadius / ringCount) * i
                    drawCircle(
                        color = TacticalGridLine,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Radar Crosshair Axes
                drawLine(
                    color = TacticalGridLine,
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = TacticalGridLine,
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1.dp.toPx()
                )

                // Sweeping Radar Beam Line
                val sweepRad = Math.toRadians((sweepAngle - 90).toDouble())
                val sweepX = (center.x + maxRadius * cos(sweepRad)).toFloat()
                val sweepY = (center.y + maxRadius * sin(sweepRad)).toFloat()

                drawLine(
                    color = TacticalGreenSecondary.copy(alpha = 0.8f),
                    start = center,
                    end = Offset(sweepX, sweepY),
                    strokeWidth = 2.dp.toPx()
                )

                // Simulated Tactical Waypoint Blip (North-East)
                val wpDist = maxRadius * 0.55f
                val wpRad = Math.toRadians(-45.0)
                val wpX = (center.x + wpDist * cos(wpRad)).toFloat()
                val wpY = (center.y + wpDist * sin(wpRad)).toFloat()

                drawCircle(
                    color = TacticalAmberPrimary,
                    radius = 5.dp.toPx(),
                    center = Offset(wpX, wpY)
                )

                // Simulated P2P Node Blip (South-West)
                val p2pDist = maxRadius * 0.70f
                val p2pRad = Math.toRadians(135.0)
                val p2pX = (center.x + p2pDist * cos(p2pRad)).toFloat()
                val p2pY = (center.y + p2pDist * sin(p2pRad)).toFloat()

                drawCircle(
                    color = EmergencyRedTertiary,
                    radius = 6.dp.toPx(),
                    center = Offset(p2pX, p2pY)
                )
            }

            // Central Self Marker
            Icon(
                imageVector = Icons.Rounded.MyLocation,
                contentDescription = "My Position",
                tint = TacticalGreenSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Radar Range Scale Selection Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(100 to "100 M", 250 to "250 M", 500 to "500 M", 1000 to "1 KM", 5000 to "5 KM").forEach { (meters, label) ->
                FilterChip(
                    selected = radarRangeMeters == meters,
                    onClick = { radarRangeMeters = meters },
                    label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TacticalGreenSecondary,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        // Live Radar HUD Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            imageVector = Icons.Rounded.GpsFixed,
                            contentDescription = null,
                            tint = TacticalGreenSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "TACTICAL RADAR HUD",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TacticalGreenSecondary
                        )
                    }

                    Text(
                        text = "RANGE: $radarRangeMeters m",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = TacticalAmberPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "BLIPS: 1 WAYPOINT (AMBER) | 1 P2P NODE (RED)",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "GRID REF: 38S U 37210 49102 | ACCURACY: ±2.5m",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TacticalMapScreenPreview() {
    EschatonToolsTheme {
        TacticalMapScreen()
    }
}
