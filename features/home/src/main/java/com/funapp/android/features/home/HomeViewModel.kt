package com.funapp.android.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funapp.android.model.Item
import com.funapp.android.platform.ui.AppSettings
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val networkService: NetworkService,
    private val favoritesService: FavoritesService,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(isLoading = true))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadData()
        observeFavorites()
        observeCarouselToggle()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            networkService.fetchHomeData()
                .onSuccess { result ->
                    val favorites = favoritesService.getFavorites().first()
                    val data = result.map { it.copy(isFavorite = favorites.contains(it.id)) }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allItems = data,
                            carouselPages = data.shuffled().chunked(2)
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error",
                            allItems = emptyList(),
                            carouselPages = emptyList()
                        )
                    }
                }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesService.getFavorites().collect { favorites ->
                _state.update { current ->
                    current.copy(
                        allItems = current.allItems.map { item ->
                            item.copy(isFavorite = favorites.contains(item.id))
                        },
                        carouselPages = current.carouselPages.map { page ->
                            page.map { item ->
                                item.copy(isFavorite = favorites.contains(item.id))
                            }
                        }
                    )
                }
            }
        }
    }

    private fun observeCarouselToggle() {
        viewModelScope.launch {
            appSettings.featuredCarouselEnabled.collect { enabled ->
                _state.update { it.copy(carouselEnabled = enabled) }
            }
        }
    }

    fun onFavoriteToggle(itemId: String) {
        viewModelScope.launch {
            favoritesService.toggleFavorite(itemId)
        }
    }

    fun onRefresh() {
        loadData()
    }
}
