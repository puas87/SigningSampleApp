package com.javilena87.fichaje.domain.usecases.login

import javax.inject.Inject

class GetValidPasswordUseCase @Inject constructor(
    private val getPassword: GetPasswordUseCase
) {

    operator fun invoke(password: String = ""): String {
        return if (getPassword.isPasswordDataChanged(password))
            password
        else
            getPassword(password)
    }

}