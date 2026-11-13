package de.goldoust.eschatontools.ui.firstaid

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Emergency
import androidx.compose.material.icons.rounded.Healing
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.goldoust.eschatontools.R
import de.goldoust.eschatontools.domain.ProtocolCategory
import de.goldoust.eschatontools.domain.SurvivalProtocol
import de.goldoust.eschatontools.domain.UrgencyLevel
import de.goldoust.eschatontools.ui.theme.EmergencyRedTertiary
import de.goldoust.eschatontools.ui.theme.EschatonToolsTheme
import de.goldoust.eschatontools.ui.theme.HazardYellow
import de.goldoust.eschatontools.ui.theme.TacticalAmberPrimary
import de.goldoust.eschatontools.ui.theme.TacticalGreenSecondary

enum class FirstAidTab(
    val titleRes: Int,
    val icon: ImageVector
) {
    EMERGENCY_PRESETS(R.string.quick_cpr, Icons.Rounded.Emergency),
    GUIDES_SEARCH(R.string.survival_first_aid_title, Icons.Rounded.MedicalServices),
    BOOKMARKS(R.string.bookmark, Icons.Rounded.Bookmark)
}

@Composable
fun getCategoryLocalizedName(category: ProtocolCategory): String {
    return when (category) {
        ProtocolCategory.ALL -> stringResource(R.string.cat_all)
        ProtocolCategory.FIRST_AID -> stringResource(R.string.cat_first_aid)
        ProtocolCategory.SURVIVAL -> stringResource(R.string.cat_all)
        ProtocolCategory.TRAUMA -> stringResource(R.string.cat_trauma)
        ProtocolCategory.ENVIRONMENTAL -> stringResource(R.string.cat_environmental)
        ProtocolCategory.HYDRATION -> stringResource(R.string.cat_hydration)
        ProtocolCategory.SHELTER -> stringResource(R.string.cat_shelter)
        ProtocolCategory.FIRE -> stringResource(R.string.cat_fire)
        ProtocolCategory.FORAGING -> stringResource(R.string.cat_foraging)
        ProtocolCategory.SIGNALLING -> stringResource(R.string.cat_signalling)
    }
}

@Composable
fun getUrgencyLocalizedName(urgency: UrgencyLevel): String {
    return when (urgency) {
        UrgencyLevel.CRITICAL -> stringResource(R.string.urgency_critical)
        UrgencyLevel.HIGH -> stringResource(R.string.urgency_high)
        UrgencyLevel.MODERATE -> stringResource(R.string.urgency_moderate)
    }
}

@Composable
fun FirstAidScreen(
    modifier: Modifier = Modifier,
    viewModel: FirstAidViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isPhoneSinglePaneDetailOpen by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isFarsi = configuration.locales.get(0).language == "fa"

    LaunchedEffect(isFarsi) {
        if (uiState.isFarsiLoaded != isFarsi) {
            viewModel.loadProtocols(isFarsi)
        }
    }

    // Intercept back button on phone when detail pane is open
    if (isPhoneSinglePaneDetailOpen && uiState.selectedProtocol != null) {
        BackHandler {
            isPhoneSinglePaneDetailOpen = false
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedDualPane = maxWidth >= 600.dp

        if (isExpandedDualPane) {
            // Dual Pane Layout for Tablet / Wide Form Factors
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Left Master List Pane (360.dp width)
                Box(
                    modifier = Modifier
                        .width(360.dp)
                        .fillMaxHeight()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    ProtocolListPane(
                        uiState = uiState,
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        onCategorySelect = { viewModel.onCategorySelect(it) },
                        onProtocolSelect = { protocol ->
                            viewModel.onProtocolSelect(protocol)
                        },
                        onQuickEmergencyClick = { protocolId ->
                            val found = uiState.allProtocols.find { it.id == protocolId }
                            viewModel.onProtocolSelect(found)
                        }
                    )
                }

                // Right Detail Pane
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (uiState.selectedProtocol != null) {
                        ProtocolDetailContent(
                            protocol = uiState.selectedProtocol!!,
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            showBackButton = false,
                            onBackClick = {}
                        )
                    } else {
                        EmptyDetailPlaceholder()
                    }
                }
            }
        } else {
            // Single Pane Layout for Phone Form Factor
            if (isPhoneSinglePaneDetailOpen && uiState.selectedProtocol != null) {
                ProtocolDetailContent(
                    protocol = uiState.selectedProtocol!!,
                    onToggleBookmark = { viewModel.toggleBookmark(it) },
                    showBackButton = true,
                    onBackClick = { isPhoneSinglePaneDetailOpen = false }
                )
            } else {
                ProtocolListPane(
                    uiState = uiState,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onCategorySelect = { viewModel.onCategorySelect(it) },
                    onProtocolSelect = { protocol ->
                        viewModel.onProtocolSelect(protocol)
                        isPhoneSinglePaneDetailOpen = true
                    },
                    onQuickEmergencyClick = { protocolId ->
                        val found = uiState.allProtocols.find { it.id == protocolId }
                        viewModel.onProtocolSelect(found)
                        isPhoneSinglePaneDetailOpen = true
                    }
                )
            }
        }
    }
}

@Composable
fun ProtocolListPane(
    uiState: FirstAidUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (ProtocolCategory) -> Unit,
    onProtocolSelect: (SurvivalProtocol) -> Unit,
    onQuickEmergencyClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(FirstAidTab.GUIDES_SEARCH) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Big Icon Sub-Section Mode Selector Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FirstAidTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    onClick = { selectedTab = tab },
                    modifier = Modifier.weight(1f),
                    color = if (isSelected) TacticalGreenSecondary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) TacticalGreenSecondary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.titleRes),
                            modifier = Modifier.size(26.dp),
                            tint = if (isSelected) Color.Black else TacticalGreenSecondary
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

        // Sub-Section 1: Quick Emergency Presets
        AnimatedVisibility(visible = selectedTab == FirstAidTab.EMERGENCY_PRESETS) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Pair("cpr_protocol", R.string.quick_cpr),
                    Pair("tourniquet_protocol", R.string.quick_bleeding),
                    Pair("water_purification", R.string.quick_water),
                    Pair("hypothermia_protocol", R.string.quick_hypothermia)
                ).forEach { (id, labelRes) ->
                    Button(
                        onClick = { onQuickEmergencyClick(id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmergencyRedTertiary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Emergency,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(labelRes).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Sub-Section 2: All Guides & Search
        AnimatedVisibility(visible = selectedTab == FirstAidTab.GUIDES_SEARCH) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Offline Search TextField
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_protocols_placeholder),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = TacticalAmberPrimary
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalAmberPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                // Category Filter Chips Bar
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ProtocolCategory.entries) { category ->
                        val isSelected = uiState.selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(category) },
                            label = {
                                Text(
                                    text = getCategoryLocalizedName(category),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalGreenSecondary,
                                selectedLabelColor = Color.Black,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Protocol Items List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredProtocols) { protocol ->
                        val isSelected = uiState.selectedProtocol?.id == protocol.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProtocolSelect(protocol) }
                                .then(
                                    if (isSelected) {
                                        Modifier.border(
                                            1.5.dp,
                                            TacticalGreenSecondary,
                                            RoundedCornerShape(12.dp)
                                        )
                                    } else Modifier
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = getCategoryLocalizedName(protocol.category).uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalGreenSecondary
                                    )

                                    UrgencyBadge(urgency = protocol.urgency)
                                }

                                Text(
                                    text = protocol.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = protocol.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sub-Section 3: Bookmarks List
        AnimatedVisibility(visible = selectedTab == FirstAidTab.BOOKMARKS) {
            val bookmarkedList = uiState.allProtocols.filter { it.isBookmarked }
            if (bookmarkedList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.bookmarked_protocols, 0),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(bookmarkedList) { protocol ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProtocolSelect(protocol) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = protocol.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = protocol.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProtocolDetailContent(
    protocol: SurvivalProtocol,
    onToggleBookmark: (String) -> Unit,
    showBackButton: Boolean,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button & Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = TacticalAmberPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = getCategoryLocalizedName(protocol.category).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TacticalGreenSecondary
                )
            }

            IconButton(onClick = { onToggleBookmark(protocol.id) }) {
                Icon(
                    imageVector = if (protocol.isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    contentDescription = stringResource(R.string.bookmark),
                    tint = if (protocol.isBookmarked) TacticalAmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Title & Urgency Level Card
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UrgencyBadge(urgency = protocol.urgency)
                    Text(
                        text = "100% OFFLINE MANUAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = protocol.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = protocol.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Required Tools Section (if any)
        if (protocol.requiredTools.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Build,
                            contentDescription = null,
                            tint = TacticalAmberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.required_tools),
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TacticalAmberPrimary
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        protocol.requiredTools.forEach { tool ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tool, fontSize = 12.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }

        // Critical Warnings Callout Box (if any)
        if (protocol.warnings.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = EmergencyRedTertiary.copy(alpha = 0.15f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRedTertiary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = EmergencyRedTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.critical_warnings),
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRedTertiary
                        )
                    }

                    protocol.warnings.forEach { warning ->
                        Text(
                            text = "• $warning",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Step-by-Step Interactive Guide Section
        Text(
            text = stringResource(R.string.step_by_step_instructions),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TacticalAmberPrimary
        )

        protocol.steps.forEach { step ->
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(TacticalGreenSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${step.stepNumber}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    step.warning?.let { stepWarn ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HazardYellow.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Warning,
                                    contentDescription = null,
                                    tint = HazardYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stepWarn,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HazardYellow
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pro Tips Box (if any)
        if (protocol.proTips.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = TacticalAmberPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.pro_tips_advice),
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TacticalAmberPrimary
                        )
                    }

                    protocol.proTips.forEach { tip ->
                        Text(
                            text = "💡 $tip",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UrgencyBadge(urgency: UrgencyLevel) {
    val (bgColor, textColor) = when (urgency) {
        UrgencyLevel.CRITICAL -> Pair(EmergencyRedTertiary, Color.White)
        UrgencyLevel.HIGH -> Pair(HazardYellow, Color.Black)
        UrgencyLevel.MODERATE -> Pair(TacticalAmberPrimary.copy(alpha = 0.3f), TacticalAmberPrimary)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = getUrgencyLocalizedName(urgency).uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun EmptyDetailPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MedicalServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = stringResource(R.string.select_protocol_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirstAidScreenPreview() {
    EschatonToolsTheme {
        FirstAidScreen()
    }
}
