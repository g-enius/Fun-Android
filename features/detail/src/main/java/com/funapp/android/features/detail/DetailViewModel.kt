package com.funapp.android.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funapp.android.model.TechnologyDescriptions
import com.funapp.android.platform.ui.AppSettings
import com.funapp.android.services.ai.AiService
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val itemId: String,
    private val networkService: NetworkService,
    private val favoritesService: FavoritesService,
    private val aiService: AiService,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState(isLoading = true))
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        loadDetails()
        observeFavorites()
        checkAiAvailability()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            networkService.fetchItemDetails(itemId)
                .onSuccess { item ->
                    val isFav = favoritesService.isFavorite(item.id)
                    val detailedDescription = TechnologyDescriptions.description(item.id)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            item = item.copy(isFavorite = isFav),
                            detailedDescription = detailedDescription,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    private fun checkAiAvailability() {
        viewModelScope.launch {
            val enabled = appSettings.aiSummaryEnabled.value
            val available = if (enabled) aiService.isAvailable() else false
            _state.update { it.copy(showAiSummary = enabled && available) }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesService.getFavorites().collect { favorites ->
                _state.update { current ->
                    val currentItem = current.item ?: return@collect
                    current.copy(
                        item = currentItem.copy(isFavorite = favorites.contains(currentItem.id))
                    )
                }
            }
        }
    }

    fun onFavoriteToggle() {
        viewModelScope.launch {
            val item = _state.value.item ?: return@launch
            favoritesService.toggleFavorite(item.id)
        }
    }

    fun onGenerateSummary() {
        val description = _state.value.detailedDescription
            ?: _state.value.item?.description
            ?: return
        viewModelScope.launch {
            _state.update { it.copy(isAiSummarizing = true, aiSummaryError = null) }
            aiService.summarize(description)
                .onSuccess { summary ->
                    _state.update { it.copy(aiSummary = summary, isAiSummarizing = false) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            aiSummaryError = error.message ?: "Summarisation failed",
                            isAiSummarizing = false
                        )
                    }
                }
        }
    }

    fun onRefresh() {
        loadDetails()
    }
}
