package com.example.prog7314_poe.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repo: TestAuthRepository
    private lateinit var vm: AuthViewModel

    @Before fun setup() {
        Dispatchers.setMain(dispatcher)
        repo = TestAuthRepository()
        vm = AuthViewModel(repo)
    }

    @After fun tearDown() { Dispatchers.resetMain() }

    //Test to check if multiple accounts under one email
    @Test fun signUp_duplicateEmail_errors() = runTest {
        repo.seedUser("dup@user.com", "secret123")
        vm.signUp("dup@user.com", "secret123")
        val msg = (vm.state.value as AuthUiState.Error).message.lowercase()
        assertThat(msg, containsString("already in use"))
    }

    //Tests if password checker works
    @Test fun signIn_wrongPassword_errors() = runTest {
        repo.seedUser("me@user.com", "secret123")
        vm.signIn("me@user.com", "wrongpass")
        val msg = (vm.state.value as AuthUiState.Error).message.lowercase()
        assertThat(msg, anyOf(containsString("incorrect"), containsString("invalid")))
    }
}
