package com.funapp.android.model

enum class FeatureFlag(val key: String, val defaultEnabled: Boolean = true) {
    ENABLE_SEARCH("enable_search"),
    ENABLE_FAVORITES("enable_favorites"),
    ENABLE_PROFILE_EDITING("enable_profile_editing", defaultEnabled = false)
}
