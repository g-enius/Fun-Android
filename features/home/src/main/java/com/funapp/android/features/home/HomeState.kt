package com.funapp.android.features.home

import com.funapp.android.model.Item

data class HomeState(
    val isLoading: Boolean = false,
    val carouselPages: List<List<Item>> = emptyList(),
    val allItems: List<Item> = emptyList(),
    val carouselEnabled: Boolean = true,
    val error: String? = null
)
