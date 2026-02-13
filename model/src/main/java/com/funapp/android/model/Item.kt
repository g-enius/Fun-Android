package com.funapp.android.model

data class Item(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String = "",
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)
