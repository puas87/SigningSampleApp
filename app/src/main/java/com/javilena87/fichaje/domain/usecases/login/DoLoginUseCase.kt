package com.javilena87.fichaje.domain.usecases.login

import javax.inject.Inject

class DoLoginUseCase @Inject constructor(
    private val getLoginRequest: GetLoginRequestUseCase,
    private val storeUserData: StoreUserDataUseCase
) {

    suspend operator fun invoke(userName: String, password: String): LoginResult {
        try {
            if (userName.isBlank()) return LoginResult(false, userName)
            if (password.isBlank()) return LoginResult(false, userName)
            getLoginRequest(userName, password).apply {
                if (this) {
                    storeUserData(userName, password)
                }
                return LoginResult(this, userName, userName.isNotBlank())
            }
        } catch (e: Exception) {
            return LoginResult(false, userName)
        }
    }
}

data class LoginResult(val requestOk: Boolean, val userName: String, val userRemembered: Boolean = false)