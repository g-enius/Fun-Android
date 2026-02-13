package com.funapp.android.features.detail

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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkService: NetworkService
    private lateinit var favoritesService: FavoritesService

    private val mockItem = Item("1", "Test Item", "Test Description", "Category A")

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        networkService = mockk()
        favoritesService = mockk()
        every { favoritesService.getFavorites() } returns flowOf(emptySet())
        coEvery { favoritesService.isFavorite(any()) } returns false
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads item details successfully`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)

        val viewModel = DetailViewModel("1", networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNotNull(state.item)
        assertEquals("Test Item", state.item?.title)
        assertNull(state.error)
    }

    @Test
    fun `handles error loading details`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.failure(Exception("Not found"))

        val viewModel = DetailViewModel("1", networkService, favoritesService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Not found", state.error)
    }

    @Test
    fun `toggle favorite calls service`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { favoritesService.toggleFavorite(any()) } returns Unit

        val viewModel = DetailViewModel("1", networkService, favoritesService)
        advanceUntilIdle()

        viewModel.onFavoriteToggle()
        advanceUntilIdle()

        coVerify { favoritesService.toggleFavorite("1") }
    }
}
