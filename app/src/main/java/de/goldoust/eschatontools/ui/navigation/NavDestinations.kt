package de.goldoust.eschatontools.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CellTower
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavDestination(
    val title: String,
    val icon: ImageVector
) {
    COMPASS("Compass", Icons.Rounded.Explore),
    BEACON("Beacon SOS", Icons.Rounded.Warning),
    FIRST_AID("First Aid", Icons.Rounded.MedicalServices),
    P2P_MESH("P2P Mesh", Icons.Rounded.CellTower)
}
