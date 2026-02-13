package com.funapp.android.features.search

import com.funapp.android.model.Item

data class SearchState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<Item> = emptyList(),
    val error: String? = null
)
