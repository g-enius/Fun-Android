package com.funapp.android.features.detail

import com.funapp.android.model.Item

data class DetailState(
    val isLoading: Boolean = false,
    val item: Item? = null,
    val error: String? = null,
    val detailedDescription: String? = null,
    val showAiSummary: Boolean = false,
    val aiSummary: String? = null,
    val isAiSummarizing: Boolean = false,
    val aiSummaryError: String? = null
)
