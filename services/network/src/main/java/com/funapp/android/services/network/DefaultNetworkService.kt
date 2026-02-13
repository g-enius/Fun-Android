package com.funapp.android.services.network

import com.funapp.android.model.Item
import com.funapp.android.model.SearchResult
import com.funapp.android.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DefaultNetworkService : NetworkService {

    override suspend fun fetchHomeData(): Result<List<Item>> = withContext(Dispatchers.IO) {
        delay(500) // Simulate network delay
        Result.success(
            listOf(
                Item("1", "Sample Item 1", "This is the first sample item", "Category A",
                    "https://picsum.photos/200/200?random=1"),
                Item("2", "Sample Item 2", "Another demo item", "Category A",
                    "https://picsum.photos/200/200?random=2"),
                Item("3", "Sample Item 3", "Yet another item", "Category B",
                    "https://picsum.photos/200/200?random=3"),
                Item("4", "Fourth demo item", "Fourth item description", "Category B",
                    "https://picsum.photos/200/200?random=4"),
                Item("5", "The fifth item", "Fifth item description", "Category C",
                    "https://picsum.photos/200/200?random=5")
            )
        )
    }

    override suspend fun fetchItemDetails(itemId: String): Result<Item> = withContext(Dispatchers.IO) {
        delay(300)
        Result.success(
            Item(
                id = itemId,
                title = "Item $itemId Details",
                description = "This is the detailed view for item $itemId. In a real app, this would show more information.",
                category = "Category ${('A'.code + itemId.hashCode() % 3).toChar()}",
                imageUrl = "https://picsum.photos/400/300?random=$itemId"
            )
        )
    }

    override suspend fun searchItems(query: String): Result<SearchResult> = withContext(Dispatchers.IO) {
        delay(300)
        val allItems = listOf(
            Item("1", "Apple", "Fresh apple", category = "Fruit"),
            Item("2", "Banana", "Yellow banana", category = "Fruit"),
            Item("3", "Carrot", "Orange carrot", category = "Vegetable"),
            Item("4", "Dragon fruit", "Exotic dragon fruit", category = "Fruit"),
            Item("5", "Eggplant", "Purple eggplant", category = "Vegetable")
        )
        val filtered = allItems.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
        }
        Result.success(SearchResult(query = query, results = filtered, totalCount = filtered.size))
    }

    override suspend fun fetchUserProfile(userId: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        delay(400)
        Result.success(
            UserProfile(
                id = userId,
                name = "User $userId",
                email = "user$userId@funapp.demo",
                bio = "This is a demo user profile."
            )
        )
    }
}
