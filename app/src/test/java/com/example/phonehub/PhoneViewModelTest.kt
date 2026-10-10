package com.example.phonehub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PhoneViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository: PhoneRepository = mockk(relaxed = true)
    private lateinit var viewModel: PhoneViewModel

    private val fakePhones = listOf(
        PhoneEntity("1", "Apple", "iPhone 15", 35000.0, 6, 128, 3349, "url1", false),
        PhoneEntity("2", "Samsung", "Galaxy S24", 42000.0, 8, 256, 4000, "url2", true),
        PhoneEntity("3", "Xiaomi", "Redmi Note 13", 9000.0, 6, 128, 5000, "url3", false)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.phones } returns flowOf(fakePhones)
        viewModel = PhoneViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewmodel initialized, phones list matches repository flow`() = runTest {
        // Підключаємо observeForever, щоб LiveData активувала свій потоковий зв'язок у тестах
        val observer = androidx.lifecycle.Observer<List<PhoneEntity>> {}
        viewModel.phones.observeForever(observer)

        testScheduler.advanceUntilIdle()

        val result = viewModel.phones.value
        assertNotNull(result)
        assertEquals(3, result?.size)
        assertEquals("iPhone 15", result?.first()?.model)

        viewModel.phones.removeObserver(observer)
    }

    @Test
    fun `search query filters phones correctly`() = runTest {
        val observer = androidx.lifecycle.Observer<List<PhoneEntity>> {}
        viewModel.phones.observeForever(observer)

        testScheduler.advanceUntilIdle()
        viewModel.onSearchQueryChanged("Samsung")
        testScheduler.advanceUntilIdle()

        val result = viewModel.phones.value
        assertNotNull(result)
        assertEquals(1, result?.size)
        assertEquals("Galaxy S24", result?.first()?.model)

        viewModel.phones.removeObserver(observer)
    }

    @Test
    fun `sorting by price ascending works correctly`() = runTest {
        val observer = androidx.lifecycle.Observer<List<PhoneEntity>> {}
        viewModel.phones.observeForever(observer)

        testScheduler.advanceUntilIdle()
        viewModel.setSortOrder(PhoneViewModel.SortOrder.PRICE_ASC)
        testScheduler.advanceUntilIdle()

        val result = viewModel.phones.value
        assertNotNull(result)
        assertEquals("Xiaomi", result?.first()?.brand)
        assertEquals("Samsung", result?.last()?.brand)

        viewModel.phones.removeObserver(observer)
    }

    @Test
    fun `toggleFavorite calls repository update correctly`() = runTest {
        val phone = fakePhones[0]
        viewModel.toggleFavorite(phone)
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.update(any()) }
    }

    @Test
    fun `favoritePhones contains only favorite items`() = runTest {
        val observer = androidx.lifecycle.Observer<List<PhoneEntity>> {}
        viewModel.favoritePhones.observeForever(observer)

        testScheduler.advanceUntilIdle()

        val favorites = viewModel.favoritePhones.value
        assertNotNull(favorites)
        assertEquals(1, favorites?.size)
        assertEquals("Galaxy S24", favorites?.first()?.model)

        viewModel.favoritePhones.removeObserver(observer)
    }

    @Test
    fun `toggleCompare adds phones and respects max 2 limit`() = runTest {
        val observer = androidx.lifecycle.Observer<List<PhoneEntity>> {}
        viewModel.comparedPhones.observeForever(observer)

        testScheduler.advanceUntilIdle()

        val phone1 = fakePhones[0]
        val phone2 = fakePhones[1]
        val phone3 = fakePhones[2]

        viewModel.toggleCompare(phone1)
        viewModel.toggleCompare(phone2)
        testScheduler.advanceUntilIdle()

        var compared = viewModel.comparedPhones.value
        assertNotNull(compared)
        assertEquals(2, compared?.size)
        assertEquals(phone1.id, compared?.first()?.id)

        viewModel.toggleCompare(phone3)
        testScheduler.advanceUntilIdle()

        compared = viewModel.comparedPhones.value
        assertNotNull(compared)
        assertEquals(2, compared?.size)
        assertEquals(phone2.id, compared?.first()?.id)
        assertEquals(phone3.id, compared?.last()?.id)

        viewModel.comparedPhones.removeObserver(observer)
    }
}