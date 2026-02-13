package com.funapp.android.features.profiledetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funapp.android.platform.ui.utils.viewModelFactory
import com.funapp.android.services.network.NetworkService

@Composable
fun ProfileDetailScreen(
    userId: String,
    networkService: NetworkService,
    onBack: () -> Unit
) {
    val viewModel: ProfileDetailViewModel = viewModel(
        factory = viewModelFactory { ProfileDetailViewModel(userId, networkService) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileDetailContent(
        state = state,
        onBack = onBack,
        onRefresh = viewModel::onRefresh
    )
}
