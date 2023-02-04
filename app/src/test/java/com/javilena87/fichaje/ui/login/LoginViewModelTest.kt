package com.javilena87.fichaje.ui.login

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.javilena87.fichaje.MainCoroutineRule
import com.javilena87.fichaje.data.prefs.FakeFichajeSharedPrefs
import com.javilena87.fichaje.data.repository.FakeFichajeRepository
import com.javilena87.fichaje.domain.usecases.*
import com.javilena87.fichaje.getOrAwaitValue
import com.javilena87.fichaje.presentation.login.LoginViewModel
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

    private val userNameUseCase = GetUserNameUseCase(fakeSharedPrefs)
    private val clearDataUseCase = ClearDataUseCase(fakeSharedPrefs)
    private val getUserRememberedUseCase = GetUserRememberedUseCase(fakeSharedPrefs)
    private val userDataUseCase = GetUserDataUseCase(getUserRememberedUseCase,
        userNameUseCase)
    private val doLoginUseCaseDefault = DoLoginUseCase(fakeRepository, fakeSharedPrefs, userNameUseCase)
    private val doLoginUseCaseException = DoLoginUseCase(fakeExceptionRepository, fakeSharedPrefs, userNameUseCase)
    private val doLoginUseCaseRequestError = DoLoginUseCase(fakeErrorRepository, fakeSharedPrefs, userNameUseCase)

    private val loginViewModelHappyPath = LoginViewModel(doLoginUseCaseDefault, clearDataUseCase, userDataUseCase)
    private val loginViewModelException = LoginViewModel(doLoginUseCaseException, clearDataUseCase, userDataUseCase)
    private val loginViewModelRequestError = LoginViewModel(doLoginUseCaseRequestError, clearDataUseCase, userDataUseCase)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `given an login result not null when init result then login result value is null`() {
        loginViewModelHappyPath.initResult()

        val value = loginViewModelHappyPath.loginResult.getOrAwaitValue()

        assertThat(value, nullValue())
    }

    @Test
    fun `given an username and password when login then login result value is true`() {
        loginViewModelHappyPath.login("username", "password")

        val value = loginViewModelHappyPath.loginResult.getOrAwaitValue()

        assertThat(value, `is`(true))
    }

    @Test
    fun `given an username and password when login then user data is stored`() {
        loginViewModelHappyPath.login("username", "password")

        assertEquals("User name is not equals", "username", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "password", fakeSharedPrefs.getPassword())
    }


    @Test
    fun `given username and password stored when login with same data then data is the same`() {
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModelHappyPath.login("username", "password")

        assertEquals("User name is not equals", "username", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "password", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given username and password stored when login with different data then data changes`() {
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModelHappyPath.login("newUsername", "newPassword")

        assertEquals("User name is not equals", "newUsername", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "newPassword", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and password when repository gets error then login result value is true`() {
        loginViewModelException.login("username", "password")

        val value = loginViewModelException.loginResult.getOrAwaitValue()

        assertThat(value, `is`(false))
    }

    @Test
    fun `given an username and password remembered when repository gets error then has to inform with view data`() {
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModelRequestError.login("username", "password")

        val value = loginViewModelRequestError.loginFormState.getOrAwaitValue()

        assertEquals("User name is not equals", "username", value.userRemembered)
        assertEquals("userRememberedVisibility is not visible", View.VISIBLE, value.userRememberedVisibility)
        assertEquals("userNameVisibility is not invisible", View.INVISIBLE, value.userNameVisibility)
        assertEquals("passwordVisibility is not visible", View.VISIBLE, value.passwordVisibility)
        assertFalse("dataCleared is not false", value.dataCleared)
    }

    @Test
    fun `given an username and password when repository gets error then user data is not stored`() {
        loginViewModelException.login("username", "password")

        assertEquals("User name is not empty", "", fakeSharedPrefs.getUsername())
        assertEquals("Password is not empty", "", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and password when login and init result then login result value is null`() {
        loginViewModelHappyPath.login("username", "password")

        val valueLogin = loginViewModelHappyPath.loginResult.getOrAwaitValue()

        assertThat(valueLogin, `is`(true))

        loginViewModelHappyPath.initResult()

        val valueReset = loginViewModelHappyPath.loginResult.getOrAwaitValue()

        assertThat(valueReset, nullValue())
    }

    @Test
    fun `given an username and password when user logs in then saved username and password`() {
        loginViewModelHappyPath.login("username", "password")

        assertEquals("User name is not equals", "username", fakeSharedPrefs.getUsername())
        assertEquals("Password is not equals", "password", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and password when user logs in and clears data then saved username and password`() {
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModelHappyPath.clearSharedData()

        assertEquals("User name is not empty", "", fakeSharedPrefs.getUsername())
        assertEquals("Password is not empty", "", fakeSharedPrefs.getPassword())
    }

    @Test
    fun `given an username and a password stored when user gets its data then has to inform data stored`() {
        fakeSharedPrefs.setUserData("username", "password")

        loginViewModelHappyPath.initUserRemembered()

        val value = loginViewModelHappyPath.loginFormState.getOrAwaitValue()

        assertEquals("User name is not equals", "username", value.userRemembered)
        assertEquals("userRememberedVisibility is not visible", View.VISIBLE, value.userRememberedVisibility)
        assertEquals("userNameVisibility is not invisible", View.INVISIBLE, value.userNameVisibility)
        assertEquals("passwordVisibility is not invisible", View.INVISIBLE, value.passwordVisibility)
        assertFalse("dataCleared is not false", value.dataCleared)
    }


}