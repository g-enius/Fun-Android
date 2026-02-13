package com.funapp.android.features.profiledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funapp.android.services.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileDetailViewModel(
    private val userId: String,
    private val networkService: NetworkService
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileDetailState(isLoading = true))
    val state: StateFlow<ProfileDetailState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            networkService.fetchUserProfile(userId)
                .onSuccess { profile ->
                    _state.value = ProfileDetailState(isLoading = false, profile = profile)
                }
                .onFailure { error ->
                    _state.value = ProfileDetailState(
                        isLoading = false,
                        error = error.message ?: "Unknown error"
                    )
                }
        }
    }

    fun onRefresh() {
        loadProfile()
    }
}
