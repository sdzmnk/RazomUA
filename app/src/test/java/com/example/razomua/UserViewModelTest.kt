package com.example.razomua.viewmodel

import com.example.razomua.model.User
import com.example.razomua.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = UserViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUserByEmail_returnsCorrectUser() = runTest {
        val user1 = User(id = 1, password = "Alice", email = "alice@test.com")
        val user2 = User(id = 2, password = "Bob", email = "bob@test.com")

        val usersField = UserViewModel::class.java.getDeclaredField("_users")
        usersField.isAccessible = true
        val list = usersField.get(viewModel) as MutableList<User>
        list.add(user1)
        list.add(user2)

        assertEquals(user1, viewModel.getUserByEmail("alice@test.com"))
        assertEquals(user2, viewModel.getUserByEmail("bob@test.com"))
        assertNull(viewModel.getUserByEmail("charlie@test.com"))
    }

    @Test
    fun usersFlow_initiallyEmpty() = runTest {
        assertTrue(viewModel.usersFlow.value.isEmpty())
    }
}
