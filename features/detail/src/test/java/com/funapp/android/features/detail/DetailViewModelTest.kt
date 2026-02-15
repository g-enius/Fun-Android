package com.funapp.android.features.detail

import com.funapp.android.model.Item
import com.funapp.android.platform.ui.AppSettings
import com.funapp.android.services.ai.AiService
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkService: NetworkService
    private lateinit var favoritesService: FavoritesService
    private lateinit var aiService: AiService
    private lateinit var appSettings: AppSettings

    private val mockItem = Item("1", "Test Item", "Test Description", category = "Category A")

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        networkService = mockk()
        favoritesService = mockk()
        aiService = mockk()
        appSettings = AppSettings()
        every { favoritesService.getFavorites() } returns flowOf(emptySet())
        coEvery { favoritesService.isFavorite(any()) } returns false
        coEvery { aiService.isAvailable() } returns false
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(itemId: String = "1") =
        DetailViewModel(itemId, networkService, favoritesService, aiService, appSettings)

    @Test
    fun `loads item details successfully`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)

        val viewModel = createViewModel()
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

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Not found", state.error)
    }

    @Test
    fun `toggle favorite calls service`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { favoritesService.toggleFavorite(any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onFavoriteToggle()
        advanceUntilIdle()

        coVerify { favoritesService.toggleFavorite("1") }
    }

    @Test
    fun `showAiSummary true when toggle on and service available`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { aiService.isAvailable() } returns true

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.showAiSummary)
    }

    @Test
    fun `showAiSummary false when toggle off`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { aiService.isAvailable() } returns true
        appSettings.setAiSummaryEnabled(false)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.showAiSummary)
    }

    @Test
    fun `showAiSummary false when service unavailable`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { aiService.isAvailable() } returns false

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.showAiSummary)
    }

    @Test
    fun `generateSummary sets summary text on success`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { aiService.isAvailable() } returns true
        coEvery { aiService.summarize(any()) } returns Result.success("A test summary")

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGenerateSummary()
        advanceUntilIdle()

        assertEquals("A test summary", viewModel.state.value.aiSummary)
        assertFalse(viewModel.state.value.isAiSummarizing)
    }

    @Test
    fun `generateSummary handles errors`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { aiService.isAvailable() } returns true
        coEvery { aiService.summarize(any()) } returns Result.failure(Exception("Model error"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGenerateSummary()
        advanceUntilIdle()

        assertEquals("Model error", viewModel.state.value.aiSummaryError)
        assertNull(viewModel.state.value.aiSummary)
        assertFalse(viewModel.state.value.isAiSummarizing)
    }

    @Test
    fun `isAiSummarizing is false after completion`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.success(mockItem)
        coEvery { aiService.isAvailable() } returns true
        coEvery { aiService.summarize(any()) } returns Result.success("Summary")

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGenerateSummary()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isAiSummarizing)
    }

    @Test
    fun `generateSummary does nothing when no description available`() = runTest {
        coEvery { networkService.fetchItemDetails("1") } returns Result.failure(Exception("fail"))
        coEvery { aiService.isAvailable() } returns true

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onGenerateSummary()
        advanceUntilIdle()

        assertNull(viewModel.state.value.aiSummary)
        assertFalse(viewModel.state.value.isAiSummarizing)
        assertNull(viewModel.state.value.aiSummaryError)
    }
}
