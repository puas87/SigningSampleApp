package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.data.model.SigningData
import com.javilena87.fichaje.data.model.UserData
import com.javilena87.fichaje.data.remote.FichajeApi
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FichajeRepository @Inject constructor(
    private val fichajeApi: FichajeApi
) {

    suspend fun login(userName: String, password: String): UserData {
        return fichajeApi.login(userName, password)
    }

    suspend fun enter(name: String): SigningData {
        return fichajeApi.enter(name)
    }

    suspend fun exit(name: String): SigningData {
        return fichajeApi.exit(name)
    }

}