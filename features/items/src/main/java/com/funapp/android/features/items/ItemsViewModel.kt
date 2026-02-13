package com.funapp.android.features.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val networkService: NetworkService,
    private val favoritesService: FavoritesService
) : ViewModel() {

    private val _state = MutableStateFlow(ItemsState(isLoading = true))
    val state: StateFlow<ItemsState> = _state.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesService.getFavorites().collect { favoriteIds ->
                loadFavoriteItems(favoriteIds)
            }
        }
    }

    private suspend fun loadFavoriteItems(favoriteIds: Set<String>) {
        if (favoriteIds.isEmpty()) {
            _state.value = ItemsState(isLoading = false, items = emptyList())
            return
        }
        _state.value = _state.value.copy(isLoading = true)
        networkService.fetchHomeData()
            .onSuccess { allItems ->
                val favoriteItems = allItems
                    .filter { favoriteIds.contains(it.id) }
                    .map { it.copy(isFavorite = true) }
                _state.value = ItemsState(isLoading = false, items = favoriteItems)
            }
            .onFailure { error ->
                _state.value = ItemsState(
                    isLoading = false,
                    error = error.message ?: "Unknown error"
                )
            }
    }

    fun onFavoriteToggle(itemId: String) {
        viewModelScope.launch {
            favoritesService.toggleFavorite(itemId)
        }
    }
}
