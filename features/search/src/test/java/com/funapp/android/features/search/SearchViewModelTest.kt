package com.funapp.android.features.search

import com.funapp.android.model.Item
import com.funapp.android.model.SearchResult
import com.funapp.android.services.favorites.FavoritesService
import com.funapp.android.services.search.SearchService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var searchService: SearchService
    private lateinit var favoritesService: FavoritesService

    private val mockItems = listOf(
        Item("1", "Coroutines", "Async programming"),
        Item("2", "Compose", "Declarative UI")
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchService = mockk()
        favoritesService = mockk()
        every { favoritesService.getFavorites() } returns flowOf(emptySet())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty query and no results`() = runTest {
        val viewModel = SearchViewModel(searchService, favoritesService)
        val state = viewModel.state.value

        assertEquals("", state.query)
        assertFalse(state.isLoading)
        assertTrue(state.results.isEmpty())
    }

    @Test
    fun `onQueryChange updates query in state`() = runTest {
        every { searchService.search(any()) } returns flowOf(SearchResult("test", mockItems))

        val viewModel = SearchViewModel(searchService, favoritesService)
        viewModel.onQueryChange("test")

        assertEquals("test", viewModel.state.value.query)
    }

    @Test
    fun `search returns results after debounce`() = runTest {
        val result = SearchResult("coroutines", mockItems)
        every { searchService.search("coroutines") } returns flowOf(result)

        val viewModel = SearchViewModel(searchService, favoritesService)
        viewModel.onQueryChange("coroutines")

        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.results.size)
    }

    @Test
    fun `blank query clears results`() = runTest {
        val result = SearchResult("test", mockItems)
        every { searchService.search("test") } returns flowOf(result)

        val viewModel = SearchViewModel(searchService, favoritesService)
        viewModel.onQueryChange("test")
        advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.onQueryChange("")
        advanceTimeBy(350)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.results.isEmpty())
    }

    @Test
    fun `onClearQuery resets query and results`() = runTest {
        val result = SearchResult("test", mockItems)
        every { searchService.search("test") } returns flowOf(result)

        val viewModel = SearchViewModel(searchService, favoritesService)
        viewModel.onQueryChange("test")
        advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.onClearQuery()
        advanceTimeBy(350)
        advanceUntilIdle()

        assertEquals("", viewModel.state.value.query)
        assertTrue(viewModel.state.value.results.isEmpty())
    }

    @Test
    fun `favorites are reflected in search results`() = runTest {
        val favoritesFlow = MutableStateFlow<Set<String>>(emptySet())
        every { favoritesService.getFavorites() } returns favoritesFlow

        val result = SearchResult("test", mockItems)
        every { searchService.search("test") } returns flowOf(result)

        val viewModel = SearchViewModel(searchService, favoritesService)
        viewModel.onQueryChange("test")
        advanceTimeBy(350)
        advanceUntilIdle()

        favoritesFlow.value = setOf("1")
        advanceUntilIdle()

        val results = viewModel.state.value.results
        assertTrue(results.first { it.id == "1" }.isFavorite)
        assertFalse(results.first { it.id == "2" }.isFavorite)
    }

    @Test
    fun `onFavoriteToggle calls favorites service`() = runTest {
        coEvery { favoritesService.toggleFavorite(any()) } returns Unit

        val viewModel = SearchViewModel(searchService, favoritesService)
        viewModel.onFavoriteToggle("1")
        advanceUntilIdle()

        coVerify { favoritesService.toggleFavorite("1") }
    }
}
