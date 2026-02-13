package com.funapp.android.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val itemId: String,
    private val networkService: NetworkService,
    private val favoritesService: FavoritesService
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState(isLoading = true))
    val state: StateFlow<DetailState> = _state.asStateFlow()

    init {
        loadDetails()
        observeFavorites()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            networkService.fetchItemDetails(itemId)
                .onSuccess { item ->
                    val isFav = favoritesService.isFavorite(item.id)
                    _state.value = DetailState(
                        isLoading = false,
                        item = item.copy(isFavorite = isFav)
                    )
                }
                .onFailure { error ->
                    _state.value = DetailState(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesService.getFavorites().collect { favorites ->
                val currentItem = _state.value.item ?: return@collect
                _state.value = _state.value.copy(
                    item = currentItem.copy(isFavorite = favorites.contains(currentItem.id))
                )
            }
        }
    }

    fun onFavoriteToggle() {
        viewModelScope.launch {
            val item = _state.value.item ?: return@launch
            favoritesService.toggleFavorite(item.id)
        }
    }

    fun onRefresh() {
        loadDetails()
    }
}
