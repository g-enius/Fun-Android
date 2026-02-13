package com.funapp.android.features.profile

import com.funapp.android.model.UserProfile
import com.funapp.android.services.network.NetworkService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkService: NetworkService

    private val mockProfile = UserProfile(
        id = "current_user",
        name = "Test User",
        email = "test@example.com",
        bio = "A test user",
        viewsCount = 100,
        favoritesCount = 5,
        daysCount = 30
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        networkService = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        coEvery { networkService.fetchUserProfile(any()) } returns Result.success(mockProfile)

        val viewModel = ProfileViewModel(networkService)
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `loads profile successfully`() = runTest {
        coEvery { networkService.fetchUserProfile("current_user") } returns Result.success(mockProfile)

        val viewModel = ProfileViewModel(networkService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(mockProfile, state.profile)
        assertNull(state.error)
    }

    @Test
    fun `fetches profile for current_user`() = runTest {
        coEvery { networkService.fetchUserProfile("current_user") } returns Result.success(mockProfile)

        ProfileViewModel(networkService)
        advanceUntilIdle()

        coVerify { networkService.fetchUserProfile("current_user") }
    }

    @Test
    fun `handles network error`() = runTest {
        coEvery { networkService.fetchUserProfile(any()) } returns Result.failure(Exception("Connection failed"))

        val viewModel = ProfileViewModel(networkService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.profile)
        assertEquals("Connection failed", state.error)
    }

    @Test
    fun `handles error with null message`() = runTest {
        coEvery { networkService.fetchUserProfile(any()) } returns Result.failure(Exception())

        val viewModel = ProfileViewModel(networkService)
        advanceUntilIdle()

        assertEquals("Unknown error", viewModel.state.value.error)
    }

    @Test
    fun `onRefresh reloads profile`() = runTest {
        coEvery { networkService.fetchUserProfile(any()) } returns Result.success(mockProfile)

        val viewModel = ProfileViewModel(networkService)
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        coVerify(exactly = 2) { networkService.fetchUserProfile("current_user") }
    }
}
