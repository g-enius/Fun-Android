package com.funapp.android.model

data class Item(
    val id: String,
    val title: String,
    val description: String = "",
    val subtitle: String = "",
    val category: String = "",
    val iconName: String = "",
    val iconColor: String = "",
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val iosEquivalent: String? = null
)
