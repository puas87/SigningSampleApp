package com.javilena87.fichaje.ui.login

import com.javilena87.fichaje.data.remote.FichajeApi
import com.javilena87.fichaje.data.repository.DefaultFichajeRepository
import org.junit.Test


internal class LoginViewModelTest {

    @Test
    fun `given an login result not null when init result then login result value is null`() {
        val loginViewModel = LoginViewModel(DefaultFichajeRepository(FichajeApi()), null)
    }
}