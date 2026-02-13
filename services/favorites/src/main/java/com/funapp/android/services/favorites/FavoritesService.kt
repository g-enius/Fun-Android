package com.funapp.android.services.favorites

import kotlinx.coroutines.flow.Flow

interface FavoritesService {
    fun getFavorites(): Flow<Set<String>>
    suspend fun isFavorite(itemId: String): Boolean
    suspend fun toggleFavorite(itemId: String)
    suspend fun addFavorite(itemId: String)
    suspend fun removeFavorite(itemId: String)
}
