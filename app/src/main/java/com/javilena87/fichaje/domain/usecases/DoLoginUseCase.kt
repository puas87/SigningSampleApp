package com.javilena87.fichaje.domain.usecases

import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.di.RemoteSource
import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.model.UserData
import javax.inject.Inject

class DoLoginUseCase @Inject constructor(
    @RemoteSource private val loginRepository: FichajeRepository,
    @PreferencesSource private val fichajeSharedPreferencesRepository: FichajeSharedPrefsRepository,
    private val getUserName: GetUserNameUseCase
) {

    suspend operator fun invoke(userName: String, password: String): LoginResult {
        try {
            if (userName.isBlank()) return LoginResult(false, userName)
            if (password.isBlank()) return LoginResult(false, userName)
            val result = getSuccessFromResult(
                loginRepository.login(
                    userName = getUserName(userName),
                    password = if (isPasswordDataChanged(
                            password
                        )
                    ) password else getPassword(
                        password
                    )
                )
            )
            if (result) {
                if (isUserNameDataChanged(userName)) {
                    fichajeSharedPreferencesRepository.setUserName(
                        userName
                    )
                }
                if (isPasswordDataChanged(password)) {
                    fichajeSharedPreferencesRepository.setPassword(
                        password
                    )
                }
            }
            return LoginResult(result, userName, getUserName().isNotBlank())
        } catch (e: Exception) {
            return LoginResult(false, userName)
        }
    }

    private fun isUserNameDataChanged(username: String): Boolean {
        val usernameRemembered = getUserName()
        return username.isNotBlank()
                && usernameRemembered != username
    }

    private fun isPasswordDataChanged(password: String): Boolean {
        val passworRemembered = getPassword()
        return password.isNotBlank()
                && passworRemembered != password
    }

    private fun getPassword(fallback: String = ""): String {
        return fichajeSharedPreferencesRepository.getPassword(fallback)
            .ifEmpty { fallback }
    }

    private fun getSuccessFromResult(result: UserData): Boolean {
        return result.name.isNotEmpty()
    }
}

data class LoginResult(val requestOk: Boolean, val userName: String, val userRemembered: Boolean = false)