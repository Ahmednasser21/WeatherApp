package com.ahmed.weather.iti.ui.favourite

import com.ahmed.weather.iti.database.FakeLocalDataSource
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.network.FakeRemoteDataSource
import com.ahmed.weather.iti.network.FakeWeatherApiService
import com.ahmed.weather.iti.repository.FakeRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class FavouriteViewModelTest {

    private lateinit var fakeRepository: FakeRepository
    private lateinit var viewModel: FavouriteViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeRepository(FakeRemoteDataSource(FakeWeatherApiService()),
            FakeLocalDataSource()
        )
        viewModel = FavouriteViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addFav should add favourite to repository`() = runTest {
        // given:new favouriteDTo to add
        val favourite = FavouriteDTO("Test", 0.0, 0.0)

        // When: adding favouriteDTO using the ViewModel
        viewModel.addFav(favourite)

        // Then: the favouriteDTO should added
        assertTrue(fakeRepository.favouriteList.contains(favourite))
    }

    @Test
    fun `removeFav should remove favourite from repository`() = runTest {
        // Given: A favouriteDTO from repository
        val favourite = FavouriteDTO("Test", 0.0, 0.0)
        fakeRepository.favouriteList.add(favourite)

        // When:removing the favouriteDTO
        viewModel.removeFav(favourite)

        // Then: The favourite should not be in the repository favourite list
        assertTrue(!fakeRepository.favouriteList.contains(favourite))
    }

    @Test
    fun `getAllFav should emit favourites from repository`() = runTest {
        // Given:list of favourites is in the repository
        val favourites = listOf(
            FavouriteDTO("Test1", 0.0, 0.0),
            FavouriteDTO("Test2", 1.0, 1.0)
        )
        fakeRepository.favouriteList.addAll(favourites)

        // When: collecting favourites from the ViewModel
        var emittedFavourites: List<FavouriteDTO>? = null
        val job = launch(testDispatcher) {
            viewModel.favList.collect {
                emittedFavourites = it
            }
        }

        viewModel.getAllFav()
        testScheduler.advanceUntilIdle()

        // Then: the collected favourites should match the repository's favourites
        assertEquals(favourites, emittedFavourites)
        job.cancel()
    }

    @Test
    fun `getAllFav should handle errors`() = runTest {
        // Given:an error may happen in the repository
        fakeRepository.shouldThrowError = true

        // When: trying to get all favourites
        viewModel.getAllFav()
        testScheduler.advanceUntilIdle()

        // Then: the emitted favourites should be null
        var emittedFavourites: List<FavouriteDTO>? = null
        val job = launch(testDispatcher) {
            viewModel.favList.collect {
                emittedFavourites = it
            }
        }

        testScheduler.advanceUntilIdle()
        assertEquals(null, emittedFavourites)
        job.cancel()
    }
}