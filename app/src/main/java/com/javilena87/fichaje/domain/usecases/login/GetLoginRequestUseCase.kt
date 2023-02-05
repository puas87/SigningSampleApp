package com.javilena87.fichaje.domain.usecases.login

import com.javilena87.fichaje.di.RemoteSource
import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.model.UserData
import javax.inject.Inject

class GetLoginRequestUseCase @Inject constructor(
    @RemoteSource private val loginRepository: FichajeRepository,
    private val getUserData: GetUserDataUseCase) {

    suspend operator fun invoke(userName: String, password: String): Boolean {
        val userData = getUserData(userName, password)
        return getSuccessFromResult(
            loginRepository.login(
                userName = userData.userName,
                password = userData.password
            )
        )
    }

    private fun getSuccessFromResult(result: UserData): Boolean {
        return result.name.isNotEmpty()
    }
}