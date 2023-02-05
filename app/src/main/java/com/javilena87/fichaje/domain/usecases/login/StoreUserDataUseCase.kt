package com.javilena87.fichaje.domain.usecases.login

import javax.inject.Inject

class StoreUserDataUseCase @Inject constructor(
    private val getUserData: GetUserDataUseCase,
    private val setUserName: SetUserNameUseCase,
    private val setPassword: SetPasswordUseCase
) {

    operator fun invoke(userName: String, password: String) {
        if (isUserNameDataChanged(userName)) {
            setUserName(
                userName
            )
        }
        if (isPasswordDataChanged(password)) {
            setPassword(
                password
            )
        }
    }

    private fun isUserNameDataChanged(username: String): Boolean {
        val usernameRemembered = getUserData().userName
        return username.isNotBlank()
                && usernameRemembered != username
    }

    private fun isPasswordDataChanged(password: String): Boolean {
        val passwordRemembered = getUserData().password
        return password.isNotBlank()
                && passwordRemembered != password
    }
}