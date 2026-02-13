package com.funapp.android.features.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funapp.android.platform.ui.utils.viewModelFactory
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService

@Composable
fun DetailScreen(
    itemId: String,
    networkService: NetworkService,
    favoritesService: FavoritesService,
    onBack: () -> Unit
) {
    val viewModel: DetailViewModel = viewModel(
        factory = viewModelFactory { DetailViewModel(itemId, networkService, favoritesService) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailContent(
        state = state,
        onBack = onBack,
        onRefresh = viewModel::onRefresh,
        onFavoriteToggle = viewModel::onFavoriteToggle
    )
}
