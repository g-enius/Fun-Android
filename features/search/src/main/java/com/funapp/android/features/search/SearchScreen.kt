package com.funapp.android.features.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funapp.android.platform.ui.utils.viewModelFactory
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.search.SearchService

@Composable
fun SearchScreen(
    searchService: SearchService,
    favoritesService: FavoritesService,
    onNavigateToDetail: (String) -> Unit
) {
    val viewModel: SearchViewModel = viewModel(
        factory = viewModelFactory { SearchViewModel(searchService, favoritesService) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::onClearQuery,
        onItemClick = onNavigateToDetail,
        onFavoriteToggle = viewModel::onFavoriteToggle
    )
}
