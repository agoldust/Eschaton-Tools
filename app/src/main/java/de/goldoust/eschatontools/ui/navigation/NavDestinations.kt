package de.goldoust.eschatontools.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CellTower
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import de.goldoust.eschatontools.R
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary

enum class NavDestination(
    @get:StringRes val titleRes: Int,
    @get:StringRes val descRes: Int,
    val icon: ImageVector,
    val accentColor: Color
) {
    COMPASS(
        R.string.nav_compass,
        R.string.compass_desc,
        Icons.Rounded.Explore,
        TacticalAmberPrimary
    ),
    BEACON(
        R.string.nav_beacon,
        R.string.beacon_desc,
        Icons.Rounded.Warning,
        EmergencyRedTertiary
    ),
    FIRST_AID(
        R.string.nav_first_aid,
        R.string.first_aid_desc,
        Icons.Rounded.MedicalServices,
        TacticalGreenSecondary
    ),
    P2P_MESH(
        R.string.nav_p2p,
        R.string.p2p_desc,
        Icons.Rounded.CellTower,
        TacticalAmberPrimary
    )
}
