package com.javilena87.fichaje.domain.usecases

import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val getUserRemembered: GetUserRememberedUseCase,
    private val getUserName: GetUserNameUseCase
) {

    operator fun invoke(): UserData {
        return UserData(getUserName(), getUserRemembered())
    }

}

data class UserData(val userName: String, val userRememebered: Boolean)