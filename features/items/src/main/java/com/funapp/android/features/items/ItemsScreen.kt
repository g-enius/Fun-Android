package com.funapp.android.features.items

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funapp.android.platform.ui.utils.viewModelFactory
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService

@Composable
fun ItemsScreen(
    networkService: NetworkService,
    favoritesService: FavoritesService,
    onNavigateToDetail: (String) -> Unit
) {
    val viewModel: ItemsViewModel = viewModel(
        factory = viewModelFactory { ItemsViewModel(networkService, favoritesService) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    ItemsContent(
        state = state,
        onItemClick = onNavigateToDetail,
        onFavoriteToggle = viewModel::onFavoriteToggle,
        onQueryChanged = viewModel::onQueryChanged,
        onCategorySelected = viewModel::onCategorySelected
    )
}
