package com.funapp.android.model

enum class FeatureFlag(val key: String, val defaultEnabled: Boolean = true) {
    FEATURED_CAROUSEL("featured_carousel"),
    SIMULATE_ERRORS("simulate_errors", defaultEnabled = false),
    AI_SUMMARY("ai_summary")
}
