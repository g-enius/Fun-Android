package com.funapp.android.features.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funapp.android.platform.ui.utils.viewModelFactory
import com.funapp.android.services.network.NetworkService

@Composable
fun ProfileScreen(
    networkService: NetworkService,
    onGoToItems: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel(
        factory = viewModelFactory { ProfileViewModel(networkService) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileContent(
        state = state,
        onRefresh = viewModel::onRefresh,
        onGoToItems = onGoToItems,
        onLogout = onLogout
    )
}
