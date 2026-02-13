package com.funapp.android.features.home

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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkService: NetworkService
    private lateinit var favoritesService: FavoritesService

    private val mockItems = listOf(
        Item("1", "Item 1", "Desc 1"),
        Item("2", "Item 2", "Desc 2")
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

        val viewModel = HomeViewModel(networkService, favoritesService)
        val initialState = viewModel.state.value

        assertTrue(initialState.isLoading)
    }

    @Test
    fun `loads home data successfully`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)

        val viewModel = HomeViewModel(networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1, state.carouselPages.size)
        assertEquals(2, state.carouselPages.first().size)
        assertNull(state.error)
    }

    @Test
    fun `handles network error`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.failure(Exception("Network error"))

        val viewModel = HomeViewModel(networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `toggles favorite updates items`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)
        coEvery { favoritesService.toggleFavorite(any()) } returns Unit

        val viewModel = HomeViewModel(networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onFavoriteToggle("1")
        advanceUntilIdle()

        coVerify { favoritesService.toggleFavorite("1") }
    }

    @Test
    fun `favorites are reflected in carousel pages`() = runTest {
        coEvery { networkService.fetchHomeData() } returns Result.success(mockItems)
        every { favoritesService.getFavorites() } returns flowOf(setOf("1"))

        val viewModel = HomeViewModel(networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        val allItems = state.carouselPages.flatten()
        assertTrue(allItems.first { it.id == "1" }.isFavorite)
        assertFalse(allItems.first { it.id == "2" }.isFavorite)
    }
}
