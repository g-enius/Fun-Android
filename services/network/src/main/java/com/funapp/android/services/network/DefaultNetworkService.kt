package com.funapp.android.services.network

import com.funapp.android.model.Item
import com.funapp.android.model.SearchResult
import com.funapp.android.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DefaultNetworkService : NetworkService {

    private val allItems = listOf(
        Item("1", "Async/Await", "Swift's modern concurrency model uses async/await to write asynchronous code that looks synchronous. It eliminates callback pyramids and makes error handling straightforward with try/catch.", "Structured concurrency for modern Swift apps", "Concurrency", "bolt", "green"),
        Item("2", "Combine", "Apple's reactive framework for processing values over time. Combine provides a declarative Swift API for handling asynchronous events, with publishers, subscribers, and operators.", "Reactive programming with publishers and subscribers", "Reactive", "arrow.triangle.merge", "orange"),
        Item("3", "SwiftUI", "Apple's declarative UI framework that enables building user interfaces across all Apple platforms. SwiftUI uses a reactive data-driven approach with property wrappers like @State and @Binding.", "Declarative UI framework for Apple platforms", "UI", "rectangle.on.rectangle", "blue"),
        Item("4", "Coordinator", "The Coordinator pattern manages navigation flow by extracting it from view controllers. Each coordinator handles a specific flow, making navigation testable and reusable.", "Navigation pattern for managing app flow", "Architecture", "arrow.triangle.branch", "purple"),
        Item("5", "MVVM", "Model-View-ViewModel separates UI logic from business logic. The ViewModel exposes observable state that the View binds to, while the Model handles data and business rules.", "Separating concerns with observable view models", "Architecture", "square.stack.3d.up", "indigo"),
        Item("6", "SPM Modules", "Swift Package Manager enables modular architecture by splitting your app into focused packages. Each module has clear boundaries, explicit dependencies, and can be developed and tested independently.", "Modular architecture with Swift packages", "Modularity", "shippingbox", "brown"),
        Item("7", "ServiceLocator", "A service locator provides a central registry for dependencies. Components request services from the locator rather than creating them directly, enabling loose coupling and testability.", "Central registry for dependency management", "DI", "magnifyingglass.circle", "teal"),
        Item("8", "Protocol-Oriented", "Protocol-oriented programming favors protocols over inheritance. By defining behavior through protocols and providing default implementations via extensions, you get flexible and composable abstractions.", "Favoring composition over inheritance", "Patterns", "list.bullet.rectangle", "mint"),
        Item("9", "Feature Toggles", "Feature toggles (or feature flags) allow you to enable or disable features at runtime. They support A/B testing, gradual rollouts, and quick feature kill switches without redeployment.", "Runtime feature management and A/B testing", "DevOps", "switch.2", "cyan"),
        Item("10", "OSLog", "Apple's unified logging system provides structured, performant logging with levels, categories, and privacy controls. OSLog integrates with Console.app and Instruments for powerful debugging.", "Structured logging with Apple's unified system", "Debugging", "doc.text.magnifyingglass", "gray"),
        Item("11", "Swift 6", "Swift 6 introduces complete concurrency checking, ensuring data race safety at compile time. The strict concurrency model catches potential issues before they become runtime crashes.", "Complete concurrency safety at compile time", "Language", "swift", "red"),
        Item("12", "Swift Testing", "Swift Testing is a modern test framework with expressive APIs. It uses #expect and #require macros, supports parameterized tests, and integrates seamlessly with Swift concurrency.", "Modern testing with expressive macros", "Testing", "checkmark.seal", "green"),
        Item("13", "Snapshot Testing", "Snapshot testing captures the rendered output of UI components and compares against reference images. It catches unintended visual regressions automatically during CI.", "Visual regression testing for UI components", "Testing", "camera.viewfinder", "pink"),
        Item("14", "Accessibility", "Building accessible apps ensures everyone can use your software. VoiceOver, Dynamic Type, and semantic labels make apps usable for people with disabilities.", "Making apps usable for everyone", "UX", "accessibility", "blue")
    )

    override suspend fun fetchHomeData(): Result<List<Item>> = withContext(Dispatchers.IO) {
        delay(500)
        Result.success(allItems)
    }

    override suspend fun fetchItemDetails(itemId: String): Result<Item> = withContext(Dispatchers.IO) {
        delay(300)
        val item = allItems.find { it.id == itemId }
        if (item != null) {
            Result.success(item)
        } else {
            Result.success(
                Item(
                    id = itemId,
                    title = "Item $itemId",
                    description = "Details for item $itemId.",
                    category = "General"
                )
            )
        }
    }

    override suspend fun searchItems(query: String): Result<SearchResult> = withContext(Dispatchers.IO) {
        delay(300)
        val filtered = allItems.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.subtitle.contains(query, ignoreCase = true)
        }
        Result.success(SearchResult(query = query, results = filtered, totalCount = filtered.size))
    }

    override suspend fun fetchUserProfile(userId: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        delay(400)
        Result.success(
            UserProfile(
                id = userId,
                name = "Alex Developer",
                email = "alex@funapp.demo",
                bio = "iOS & Android developer passionate about clean architecture and great user experiences."
            )
        )
    }
}
