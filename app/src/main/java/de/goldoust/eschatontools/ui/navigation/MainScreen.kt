package de.goldoust.eschatontools.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import de.goldoust.eschatontools.ui.beacon.BeaconScreen
import de.goldoust.eschatontools.ui.compass.CompassScreen
import de.goldoust.eschatontools.ui.components.SurvivalPermissionBanner
import de.goldoust.eschatontools.ui.components.getRequiredEschatonPermissions
import de.goldoust.eschatontools.ui.firstaid.FirstAidScreen
import de.goldoust.eschatontools.ui.p2p.P2pScreen
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    var currentDestination by remember { mutableStateOf(NavDestination.COMPASS) }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = getRequiredEschatonPermissions()
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Shield,
                            contentDescription = null,
                            tint = TacticalAmberPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = " ESCHATON TOOLS",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TacticalAmberPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "  [OFF-GRID]",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = TacticalGreenSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavDestination.entries.forEach { destination ->
                    val selected = currentDestination == destination
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = when (destination) {
                                NavDestination.COMPASS -> TacticalAmberPrimary
                                NavDestination.BEACON -> EmergencyRedTertiary
                                NavDestination.FIRST_AID -> TacticalGreenSecondary
                                NavDestination.P2P_MESH -> TacticalAmberPrimary
                            },
                            selectedTextColor = when (destination) {
                                NavDestination.COMPASS -> TacticalAmberPrimary
                                NavDestination.BEACON -> EmergencyRedTertiary
                                NavDestination.FIRST_AID -> TacticalGreenSecondary
                                NavDestination.P2P_MESH -> TacticalAmberPrimary
                            },
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Permission request banner if missing permissions
            SurvivalPermissionBanner(
                permissionsState = permissionsState,
                modifier = Modifier.fillMaxWidth()
            )

            // Screen content switching
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentDestination) {
                    NavDestination.COMPASS -> CompassScreen()
                    NavDestination.BEACON -> BeaconScreen()
                    NavDestination.FIRST_AID -> FirstAidScreen()
                    NavDestination.P2P_MESH -> P2pScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    EschatonToolsTheme {
        MainScreen()
    }
}
