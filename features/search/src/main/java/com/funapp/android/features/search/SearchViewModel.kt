package com.funapp.android.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.search.SearchService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchService: SearchService,
    private val favoritesService: FavoritesService
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        observeSearch()
        observeFavorites()
    }

    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(null)
                    } else {
                        _state.update { it.copy(isLoading = true) }
                        searchService.search(query)
                    }
                }
                .collect { result ->
                    if (result == null) {
                        _state.update { it.copy(isLoading = false, results = emptyList()) }
                    } else {
                        _state.update {
                            it.copy(isLoading = false, results = result.results)
                        }
                    }
                }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesService.getFavorites().collect { favorites ->
                _state.update { current ->
                    current.copy(
                        results = current.results.map { item ->
                            item.copy(isFavorite = favorites.contains(item.id))
                        }
                    )
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        searchQuery.value = query
    }

    fun onClearQuery() {
        onQueryChange("")
    }

    fun onFavoriteToggle(itemId: String) {
        viewModelScope.launch {
            favoritesService.toggleFavorite(itemId)
        }
    }
}
