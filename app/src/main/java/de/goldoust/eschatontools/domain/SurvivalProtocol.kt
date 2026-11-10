package de.goldoust.eschatontools.domain

import androidx.compose.ui.graphics.vector.ImageVector

enum class ProtocolCategory(val displayName: String) {
    ALL("All Guides"),
    FIRST_AID("First Aid"),
    SURVIVAL("Wilderness Survival"),
    TRAUMA("Trauma & Bleeding"),
    ENVIRONMENTAL("Environmental Hazards"),
    HYDRATION("Water & Hydration"),
    SHELTER("Shelter & Warmth"),
    FIRE("Fire Crafting"),
    FORAGING("Foraging & Plants"),
    SIGNALLING("Emergency Signals")
}

enum class UrgencyLevel {
    CRITICAL,
    HIGH,
    MODERATE
}

data class ProtocolStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val warning: String? = null
)

data class SurvivalProtocol(
    val id: String,
    val title: String,
    val category: ProtocolCategory,
    val summary: String,
    val urgency: UrgencyLevel,
    val steps: List<ProtocolStep>,
    val warnings: List<String> = emptyList(),
    val requiredTools: List<String> = emptyList(),
    val proTips: List<String> = emptyList(),
    val isBookmarked: Boolean = false
)
