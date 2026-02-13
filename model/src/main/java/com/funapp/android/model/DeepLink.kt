package com.funapp.android.model

import android.net.Uri

sealed class DeepLink {
    data class SwitchTab(val tabIndex: Int) : DeepLink()
    data class ItemDetail(val itemId: String) : DeepLink()
    data object Profile : DeepLink()

    companion object {
        private val tabMap = mapOf(
            "home" to 0,
            "items" to 1,
            "settings" to 2
        )

        fun parse(uriString: String?): DeepLink? {
            if (uriString == null) return null
            val uri = try { Uri.parse(uriString) } catch (_: Exception) { return null }
            if (uri.scheme != "funapp") return null

            return when (uri.host) {
                "tab" -> {
                    val tabName = uri.pathSegments.firstOrNull() ?: return null
                    val index = tabMap[tabName] ?: return null
                    SwitchTab(index)
                }
                "item" -> {
                    val itemId = uri.pathSegments.firstOrNull() ?: return null
                    ItemDetail(itemId)
                }
                "profile" -> Profile
                else -> null
            }
        }
    }
}
