package com.funapp.android.features.items

import com.funapp.android.model.Item

data class ItemsState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val needsMoreCharacters: Boolean = false,
    val allItems: List<Item> = emptyList(),
    val filteredItems: List<Item> = emptyList(),
    val query: String = "",
    val selectedCategory: String? = null,
    val allCategories: List<String> = emptyList(),
    val error: String? = null
)
