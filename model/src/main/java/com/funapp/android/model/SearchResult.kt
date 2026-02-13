package com.funapp.android.model

data class SearchResult(
    val query: String,
    val results: List<Item> = emptyList(),
    val totalCount: Int = 0
)
