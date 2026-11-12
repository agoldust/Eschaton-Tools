package de.goldoust.eschatontools.ui.firstaid

import androidx.lifecycle.ViewModel
import de.goldoust.eschatontools.domain.ProtocolCategory
import de.goldoust.eschatontools.domain.SurvivalGuideRepository
import de.goldoust.eschatontools.domain.SurvivalProtocol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FirstAidUiState(
    val allProtocols: List<SurvivalProtocol> = emptyList(),
    val filteredProtocols: List<SurvivalProtocol> = emptyList(),
    val selectedProtocol: SurvivalProtocol? = null,
    val searchQuery: String = "",
    val selectedCategory: ProtocolCategory = ProtocolCategory.ALL,
    val bookmarkedIds: Set<String> = emptySet(),
    val isFarsiLoaded: Boolean = false
)

class FirstAidViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FirstAidUiState())
    val uiState: StateFlow<FirstAidUiState> = _uiState.asStateFlow()

    init {
        loadProtocols(isFarsi = false)
    }

    fun loadProtocols(isFarsi: Boolean) {
        val protocols = SurvivalGuideRepository.getProtocols(isFarsi = isFarsi)
        _uiState.update { current ->
            val updatedList = protocols.map { p ->
                p.copy(isBookmarked = current.bookmarkedIds.contains(p.id))
            }
            val selected = updatedList.find { it.id == current.selectedProtocol?.id } ?: updatedList.firstOrNull()
            val stateWithNewProtocols = current.copy(
                allProtocols = updatedList,
                selectedProtocol = selected,
                isFarsiLoaded = isFarsi
            )
            stateWithNewProtocols.copy(filteredProtocols = filterProtocols(stateWithNewProtocols))
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            val updated = current.copy(searchQuery = query)
            updated.copy(filteredProtocols = filterProtocols(updated))
        }
    }

    fun onCategorySelect(category: ProtocolCategory) {
        _uiState.update { current ->
            val updated = current.copy(selectedCategory = category)
            updated.copy(filteredProtocols = filterProtocols(updated))
        }
    }

    fun onProtocolSelect(protocol: SurvivalProtocol?) {
        _uiState.update { it.copy(selectedProtocol = protocol) }
    }

    fun toggleBookmark(protocolId: String) {
        _uiState.update { current ->
            val newBookmarks = if (current.bookmarkedIds.contains(protocolId)) {
                current.bookmarkedIds - protocolId
            } else {
                current.bookmarkedIds + protocolId
            }
            val updatedList = current.allProtocols.map { p ->
                if (p.id == protocolId) p.copy(isBookmarked = newBookmarks.contains(protocolId))
                else p.copy(isBookmarked = newBookmarks.contains(p.id))
            }
            val stateWithBookmarks = current.copy(
                bookmarkedIds = newBookmarks,
                allProtocols = updatedList
            )
            stateWithBookmarks.copy(filteredProtocols = filterProtocols(stateWithBookmarks))
        }
    }

    fun clearSearch() {
        onSearchQueryChange("")
    }

    private fun filterProtocols(state: FirstAidUiState): List<SurvivalProtocol> {
        return state.allProtocols.filter { protocol ->
            val matchesCategory = state.selectedCategory == ProtocolCategory.ALL ||
                    protocol.category == state.selectedCategory

            val query = state.searchQuery.trim().lowercase()
            val matchesSearch = query.isEmpty() ||
                    protocol.title.lowercase().contains(query) ||
                    protocol.summary.lowercase().contains(query) ||
                    protocol.category.displayName.lowercase().contains(query) ||
                    protocol.requiredTools.any { it.lowercase().contains(query) } ||
                    protocol.steps.any {
                        it.title.lowercase().contains(query) || it.description.lowercase().contains(query)
                    }

            matchesCategory && matchesSearch
        }
    }
}
