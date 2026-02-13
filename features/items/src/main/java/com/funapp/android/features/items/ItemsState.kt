package com.funapp.android.features.items

import com.funapp.android.model.Item

data class ItemsState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val error: String? = null
)
