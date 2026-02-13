package com.funapp.android.services.network

import com.funapp.android.model.Item
import com.funapp.android.model.SearchResult
import com.funapp.android.model.UserProfile

interface NetworkService {
    suspend fun fetchHomeData(): Result<List<Item>>
    suspend fun fetchItemDetails(itemId: String): Result<Item>
    suspend fun searchItems(query: String): Result<SearchResult>
    suspend fun fetchUserProfile(userId: String): Result<UserProfile>
}
