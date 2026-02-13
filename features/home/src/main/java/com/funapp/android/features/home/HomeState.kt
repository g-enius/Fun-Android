package com.funapp.android.features.home

import com.funapp.android.model.Item

data class HomeState(
    val isLoading: Boolean = false,
    val carouselPages: List<List<Item>> = emptyList(),
    val error: String? = null
)
