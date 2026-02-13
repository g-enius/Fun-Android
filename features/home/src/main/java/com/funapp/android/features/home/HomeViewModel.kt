package com.funapp.android.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funapp.android.model.Item
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val networkService: NetworkService,
    private val favoritesService: FavoritesService
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(isLoading = true))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var allItems: List<Item> = emptyList()

    init {
        loadData()
        observeFavorites()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            networkService.fetchHomeData()
                .onSuccess { data ->
                    allItems = data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            carouselPages = data.chunked(2)
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

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesService.getFavorites().collect { favorites ->
                _state.update { current ->
                    current.copy(
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

    fun onFavoriteToggle(itemId: String) {
        viewModelScope.launch {
            favoritesService.toggleFavorite(itemId)
        }
    }

    fun onRefresh() {
        loadData()
    }
}
