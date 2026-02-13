package com.funapp.android.services.favorites

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultFavoritesService(
    private val sharedPreferences: SharedPreferences
) : FavoritesService {

    private val _favorites = MutableStateFlow<Set<String>>(loadFavorites())

    override fun getFavorites(): Flow<Set<String>> = _favorites.asStateFlow()

    override suspend fun isFavorite(itemId: String): Boolean {
        return _favorites.value.contains(itemId)
    }

    override suspend fun toggleFavorite(itemId: String) {
        if (isFavorite(itemId)) {
            removeFavorite(itemId)
        } else {
            addFavorite(itemId)
        }
    }

    override suspend fun addFavorite(itemId: String) {
        _favorites.update { current -> current + itemId }
        saveFavorites(_favorites.value)
    }

    override suspend fun removeFavorite(itemId: String) {
        _favorites.update { current -> current - itemId }
        saveFavorites(_favorites.value)
    }

    private fun loadFavorites(): Set<String> {
        val raw = sharedPreferences.getString(KEY_FAVORITES, null) ?: return emptySet()
        return raw.split(",").filter { it.isNotBlank() }.toSet()
    }

    private fun saveFavorites(favorites: Set<String>) {
        sharedPreferences.edit()
            .putString(KEY_FAVORITES, favorites.joinToString(","))
            .apply()
    }

    companion object {
        private const val KEY_FAVORITES = "favorites"
    }
}
