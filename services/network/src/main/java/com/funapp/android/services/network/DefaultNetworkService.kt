package com.funapp.android.services.network

import com.funapp.android.model.Item
import com.funapp.android.model.SearchResult
import com.funapp.android.model.UserProfile
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class DefaultNetworkService(
    private val simulateErrors: StateFlow<Boolean>? = null
) : NetworkService {

    private val allItems = listOf(
        Item("1", "Coroutines", "Kotlin Coroutines provide a lightweight concurrency framework for Android. They simplify asynchronous programming with suspend functions, structured concurrency, and seamless integration with Jetpack libraries.", "Lightweight concurrency for modern Android apps", "Concurrency", "bolt", "green", iosEquivalent = "Async/Await"),
        Item("2", "Kotlin Flow", "Kotlin Flow is a reactive streams library built on coroutines. It provides cold asynchronous data streams with operators for transforming, combining, and collecting values over time.", "Reactive streams with coroutines integration", "Reactive", "arrow.triangle.merge", "orange", iosEquivalent = "Combine"),
        Item("3", "Jetpack Compose", "Android's modern declarative UI toolkit that simplifies and accelerates UI development. Compose uses a reactive data-driven approach with state hoisting and recomposition.", "Declarative UI toolkit for Android", "UI", "rectangle.on.rectangle", "blue", iosEquivalent = "SwiftUI"),
        Item("4", "Navigation Component", "Jetpack Navigation manages app navigation with a visual editor and type-safe arguments. It handles fragment transactions, deep links, and back stack management.", "Type-safe navigation for managing app flow", "Architecture", "arrow.triangle.branch", "purple", iosEquivalent = "Coordinator pattern"),
        Item("5", "MVVM", "Model-View-ViewModel separates UI logic from business logic. The ViewModel exposes observable state via StateFlow that Compose collects, while the Model handles data and business rules.", "Separating concerns with observable view models", "Architecture", "square.stack.3d.up", "indigo", iosEquivalent = "MVVM with @Published and ObservableObject"),
        Item("6", "Gradle Modules", "Gradle multi-module architecture splits your app into focused modules. Each module has clear boundaries, explicit dependencies, and can be built and tested independently for faster builds.", "Modular architecture with Gradle modules", "Modularity", "shippingbox", "brown", iosEquivalent = "SPM Modules"),
        Item("7", "Hilt", "Hilt is Android's recommended dependency injection library built on Dagger. It provides a standard way to incorporate DI with compile-time validation and Android lifecycle awareness.", "Compile-time dependency injection for Android", "DI", "magnifyingglass.circle", "teal", iosEquivalent = "ServiceLocator"),
        Item("8", "Interface-Oriented", "Interface-oriented design in Kotlin favors interfaces over inheritance. By defining behavior through interfaces and providing default implementations, you get flexible and testable abstractions.", "Favoring composition over inheritance", "Patterns", "list.bullet.rectangle", "mint", iosEquivalent = "Protocol-Oriented Programming"),
        Item("9", "Feature Toggles", "Feature toggles (or feature flags) allow you to enable or disable features at runtime. They support A/B testing, gradual rollouts, and quick feature kill switches without redeployment.", "Runtime feature management and A/B testing", "DevOps", "switch.2", "cyan", iosEquivalent = "Feature Toggles (same pattern)"),
        Item("10", "Timber", "Timber is a popular Android logging library that extends the standard Log API with automatic tag generation, tree-based logging, and easy configuration for debug and release builds.", "Extensible logging with tree-based architecture", "Debugging", "doc.text.magnifyingglass", "gray", iosEquivalent = "OSLog"),
        Item("11", "Kotlin 2.0", "Kotlin 2.0 brings the new K2 compiler with dramatically faster compilation, improved type inference, and better IDE support. It sets the foundation for future language evolution.", "Next-generation compiler with faster builds", "Language", "swift", "red", iosEquivalent = "Swift 6"),
        Item("12", "JUnit 5", "JUnit 5 is the modern Java/Kotlin testing framework with expressive APIs. It supports parameterized tests, nested test classes, and extensions for comprehensive test coverage.", "Modern testing with expressive assertions", "Testing", "checkmark.seal", "green", iosEquivalent = "Swift Testing"),
        Item("13", "Screenshot Testing", "Screenshot testing captures the rendered output of Compose UI components and compares against reference images. It catches unintended visual regressions automatically during CI.", "Visual regression testing for Compose UI", "Testing", "camera.viewfinder", "pink", iosEquivalent = "Snapshot Testing"),
        Item("14", "Accessibility", "Building accessible Android apps ensures everyone can use your software. TalkBack, content descriptions, and semantic properties make apps usable for people with disabilities.", "Making apps usable for everyone", "UX", "accessibility", "blue", iosEquivalent = "VoiceOver and Dynamic Type")
    )

    private fun checkSimulateErrors() {
        if (simulateErrors?.value == true) {
            throw Exception("Simulated network error")
        }
    }

    override suspend fun fetchHomeData(): Result<List<Item>> = withContext(Dispatchers.IO) {
        delay(500)
        try {
            checkSimulateErrors()
            Result.success(allItems)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchItemDetails(itemId: String): Result<Item> = withContext(Dispatchers.IO) {
        delay(300)
        try {
            checkSimulateErrors()
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchItems(query: String): Result<SearchResult> = withContext(Dispatchers.IO) {
        delay(300)
        try {
            checkSimulateErrors()
            val filtered = allItems.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.subtitle.contains(query, ignoreCase = true)
            }
            Result.success(SearchResult(query = query, results = filtered, totalCount = filtered.size))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUserProfile(userId: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        delay(400)
        try {
            checkSimulateErrors()
            Result.success(
                UserProfile(
                    id = userId,
                    name = "Alex Developer",
                    email = "alex@funapp.demo",
                    bio = "Android developer passionate about clean architecture, Jetpack Compose, and great user experiences.",
                    viewsCount = 1247,
                    favoritesCount = 38,
                    daysCount = 142
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
