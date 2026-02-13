package com.funapp.android.features.profiledetail

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
class ProfileDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var networkService: NetworkService

    private val mockProfile = UserProfile(
        id = "user_42",
        name = "Jane Doe",
        email = "jane@example.com",
        bio = "Android developer",
        viewsCount = 250,
        favoritesCount = 12,
        daysCount = 90
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

        val viewModel = ProfileDetailViewModel("user_42", networkService)
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `loads profile successfully`() = runTest {
        coEvery { networkService.fetchUserProfile("user_42") } returns Result.success(mockProfile)

        val viewModel = ProfileDetailViewModel("user_42", networkService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(mockProfile, state.profile)
        assertNull(state.error)
    }

    @Test
    fun `fetches profile for correct userId`() = runTest {
        coEvery { networkService.fetchUserProfile("user_42") } returns Result.success(mockProfile)

        ProfileDetailViewModel("user_42", networkService)
        advanceUntilIdle()

        coVerify { networkService.fetchUserProfile("user_42") }
    }

    @Test
    fun `handles network error`() = runTest {
        coEvery { networkService.fetchUserProfile(any()) } returns Result.failure(Exception("Not found"))

        val viewModel = ProfileDetailViewModel("user_42", networkService)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.profile)
        assertEquals("Not found", state.error)
    }

    @Test
    fun `handles error with null message`() = runTest {
        coEvery { networkService.fetchUserProfile(any()) } returns Result.failure(Exception())

        val viewModel = ProfileDetailViewModel("user_42", networkService)
        advanceUntilIdle()

        assertEquals("Unknown error", viewModel.state.value.error)
    }

    @Test
    fun `onRefresh reloads profile`() = runTest {
        coEvery { networkService.fetchUserProfile("user_42") } returns Result.success(mockProfile)

        val viewModel = ProfileDetailViewModel("user_42", networkService)
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        coVerify(exactly = 2) { networkService.fetchUserProfile("user_42") }
    }
}
