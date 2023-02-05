package com.javilena87.fichaje.domain.usecases.login

import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val getUserName: GetUserNameUseCase,
    private val getPassword: GetPasswordUseCase
) {

    operator fun invoke(userName: String = "", password: String = ""): UserData {
        return UserData(getUserName(userName), retrieveValidPass(password))
    }

    private fun retrieveValidPass(password: String): String {
        return if (getPassword.isPasswordDataChanged(password)) {
            password
        } else {
            getPassword(password)
        }
    }

}

data class UserData(val userName: String, val password: String) {

    fun isUserRemembered(): Boolean {
        return userName.isNotBlank()
    }

}