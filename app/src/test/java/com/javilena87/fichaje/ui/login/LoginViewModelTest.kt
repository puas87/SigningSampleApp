package com.javilena87.fichaje.ui.login

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.javilena87.fichaje.MainCoroutineRule
import com.javilena87.fichaje.data.prefs.FakeFichajeSharedPrefs
import com.javilena87.fichaje.data.repository.FakeFichajeRepository
import com.javilena87.fichaje.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test


internal class LoginViewModelTest {

    private var fakeRepository: FakeFichajeRepository = FakeFichajeRepository()
    private var fakeExceptionRepository: FakeFichajeRepository = FakeFichajeRepository(exception = true)
    private var fakeErrorRepository: FakeFichajeRepository = FakeFichajeRepository(requestFailure = true)
    private var fakeSharedPrefs: FakeFichajeSharedPrefs = FakeFichajeSharedPrefs()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `given an login result not null when init result then login result value is null`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)
        loginViewModel.initResult()

        val value = loginViewModel.loginResult.getOrAwaitValue()

        assertThat(value, nullValue())
    }

    @Test
    fun `given an username and password when login then login result value is true`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)

        loginViewModel.login("username", "password")

        val value = loginViewModel.loginResult.getOrAwaitValue()

        assertThat(value, `is`(true))
    }

    @Test
    fun `given an username and password when login then user data is stored`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)

        loginViewModel.login("username", "password")

        assertEquals("User name is not equals", "username", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "password", fakeSharedPrefs.getPassword())
    }


    @Test
    fun `given username and password stored when login with same data then data is the same`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModel.login("username", "password")

        assertEquals("User name is not equals", "username", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "password", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given username and password stored when login with different data then data changes`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModel.login("newUsername", "newPassword")

        assertEquals("User name is not equals", "newUsername", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "newPassword", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and password when repository gets error then login result value is true`() {
        val loginViewModel = LoginViewModel(fakeExceptionRepository, fakeSharedPrefs)

        loginViewModel.login("username", "password")

        val value = loginViewModel.loginResult.getOrAwaitValue()

        assertThat(value, `is`(false))
    }

    @Test
    fun `given an username and password remembered when repository gets error then has to inform with view data`() {
        val loginViewModel = LoginViewModel(fakeErrorRepository, fakeSharedPrefs)
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModel.login("username", "password")

        val value = loginViewModel.loginFormState.getOrAwaitValue()

        assertEquals("User name is not equals", "username", value.userRemembered)
        assertEquals("userRememberedVisibility is not visible", View.VISIBLE, value.userRememberedVisibility)
        assertEquals("userNameVisibility is not invisible", View.INVISIBLE, value.userNameVisibility)
        assertEquals("passwordVisibility is not visible", View.VISIBLE, value.passwordVisibility)
        assertFalse("dataCleared is not false", value.dataCleared)
    }

    @Test
    fun `given an username and password when repository gets error then user data is not stored`() {
        val loginViewModel = LoginViewModel(fakeExceptionRepository, fakeSharedPrefs)

        loginViewModel.login("username", "password")

        assertEquals("User name is not empty", "", fakeSharedPrefs.getUsername())
        assertEquals("Password is not empty", "", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and password when login and init result then login result value is null`()  {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)

        loginViewModel.login("username", "password")

        val valueLogin = loginViewModel.loginResult.getOrAwaitValue()

        assertThat(valueLogin, `is`(true))

        loginViewModel.initResult()

        val valueReset = loginViewModel.loginResult.getOrAwaitValue()

        assertThat(valueReset, nullValue())
    }

    @Test
    fun `given an username and password when user logs in then saved username and password`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)

        loginViewModel.login("username", "password")

        assertEquals("User name is not equals", "username", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "password", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and password when user logs in and clears data then saved username and password`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModel.clearSharedData()

        assertEquals("User name is not empty", "", fakeSharedPrefs.getUsername())
        assertEquals("Password is not empty", "", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and a password stored when user gets its data then has to inform data stored`() {
        val loginViewModel = LoginViewModel(fakeRepository, fakeSharedPrefs)
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModel.getUserRemembered()

        val value = loginViewModel.loginFormState.getOrAwaitValue()

        assertEquals("User name is not equals", "username", value.userRemembered)
        assertEquals("userRememberedVisibility is not visible", View.VISIBLE, value.userRememberedVisibility)
        assertEquals("userNameVisibility is not invisible", View.INVISIBLE, value.userNameVisibility)
        assertEquals("passwordVisibility is not invisible", View.INVISIBLE, value.passwordVisibility)
        assertFalse("dataCleared is not false", value.dataCleared)
    }


}