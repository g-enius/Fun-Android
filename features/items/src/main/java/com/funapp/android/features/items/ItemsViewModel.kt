package com.funapp.android.features.items

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

class ItemsViewModel(
    private val networkService: NetworkService,
    private val favoritesService: FavoritesService
) : ViewModel() {

    private val _state = MutableStateFlow(ItemsState(isLoading = true))
    val state: StateFlow<ItemsState> = _state.asStateFlow()

    init {
        loadItems()
        observeFavorites()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            networkService.fetchHomeData()
                .onSuccess { items ->
                    val categories = items.map { it.category }.distinct().sorted()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allItems = items,
                            allCategories = categories
                        )
                    }
                    applyFilters()
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
            favoritesService.getFavorites().collect { favoriteIds ->
                _state.update { current ->
                    current.copy(
                        allItems = current.allItems.map { item ->
                            item.copy(isFavorite = favoriteIds.contains(item.id))
                        },
                        filteredItems = current.filteredItems.map { item ->
                            item.copy(isFavorite = favoriteIds.contains(item.id))
                        }
                    )
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query) }
        applyFilters()
    }

    fun onCategorySelected(category: String?) {
        _state.update {
            it.copy(selectedCategory = if (it.selectedCategory == category) null else category)
        }
        applyFilters()
    }

    fun onFavoriteToggle(itemId: String) {
        viewModelScope.launch {
            favoritesService.toggleFavorite(itemId)
        }
    }

    private fun applyFilters() {
        _state.update { current ->
            var items = current.allItems

            if (current.selectedCategory != null) {
                items = items.filter { it.category == current.selectedCategory }
            }

            if (current.query.isNotBlank()) {
                val q = current.query
                items = items.filter {
                    it.title.contains(q, ignoreCase = true) ||
                        it.subtitle.contains(q, ignoreCase = true) ||
                        it.category.contains(q, ignoreCase = true) ||
                        it.description.contains(q, ignoreCase = true)
                }
            }

            current.copy(filteredItems = items)
        }
    }
}
