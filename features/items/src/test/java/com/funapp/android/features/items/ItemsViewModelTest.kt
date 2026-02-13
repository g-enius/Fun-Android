package com.funapp.android.features.items

import com.funapp.android.model.Item
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.network.NetworkService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ItemsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkService: NetworkService
    private lateinit var favoritesService: FavoritesService

    private val mockItems = listOf(
        Item("1", "Coroutines", category = "Concurrency"),
        Item("2", "Compose", category = "UI"),
        Item("3", "Navigation", category = "UI")
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        networkService = mockk()
        favoritesService = mockk()
        every { favoritesService.getFavorites() } returns flowOf(emptySet())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `loads items successfully`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(3, state.allItems.size)
        assertEquals(3, state.filteredItems.size)
        assertNull(state.error)
    }

    @Test
    fun `extracts and sorts categories`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        val categories = viewModel.state.value.allCategories
        assertEquals(listOf("Concurrency", "UI"), categories)
    }

    @Test
    fun `handles network error`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.failure(Exception("Network error"))

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `filters by category`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onCategorySelected("UI")
        val state = viewModel.state.value

        assertEquals("UI", state.selectedCategory)
        assertEquals(2, state.filteredItems.size)
        assertTrue(state.filteredItems.all { it.category == "UI" })
    }

    @Test
    fun `deselects category when same category tapped again`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onCategorySelected("UI")
        viewModel.onCategorySelected("UI")

        assertNull(viewModel.state.value.selectedCategory)
        assertEquals(3, viewModel.state.value.filteredItems.size)
    }

    @Test
    fun `search filters items by query`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onQueryChanged("Compose")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.filteredItems.size)
        assertEquals("Compose", state.filteredItems.first().title)
    }

    @Test
    fun `short query shows needs more characters`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onQueryChanged("C")

        assertTrue(viewModel.state.value.needsMoreCharacters)
        assertTrue(viewModel.state.value.filteredItems.isEmpty())
    }

    @Test
    fun `clearing query restores all items`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onQueryChanged("Compose")
        advanceUntilIdle()
        viewModel.onQueryChanged("")

        assertFalse(viewModel.state.value.needsMoreCharacters)
        assertEquals(3, viewModel.state.value.filteredItems.size)
    }

    @Test
    fun `onRefresh reloads items`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        coVerify(exactly = 2) { networkService.fetchHomeData() }
    }

    @Test
    fun `onFavoriteToggle calls favorites service`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)
        coEvery { favoritesService.toggleFavorite(any()) } returns Unit

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onFavoriteToggle("1")
        advanceUntilIdle()

        coVerify { favoritesService.toggleFavorite("1") }
    }

    @Test
    fun `favorites are reflected in items`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)
        every { favoritesService.getFavorites() } returns flowOf(setOf("2"))

        val viewModel = ItemsViewModel(networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.allItems.first { it.id == "1" }.isFavorite)
        assertTrue(state.allItems.first { it.id == "2" }.isFavorite)
    }
}
