package com.funapp.android.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funapp.android.platform.ui.utils.viewModelFactory
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService

@Composable
fun HomeScreen(
    networkService: NetworkService,
    favoritesService: FavoritesService,
    onNavigateToDetail: (String) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = viewModelFactory { HomeViewModel(networkService, favoritesService) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        onRefresh = viewModel::onRefresh,
        onItemClick = onNavigateToDetail,
        onFavoriteToggle = viewModel::onFavoriteToggle
    )
}
