package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.data.FichajeRepository
import com.javilena87.fichaje.data.model.SigningData
import com.javilena87.fichaje.data.model.UserData
import com.javilena87.fichaje.data.remote.FichajeApi
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DefaultFichajeRepository @Inject constructor(
    private val fichajeApi: FichajeApi
) : FichajeRepository {

    override suspend fun login(userName: String, password: String): UserData {
        return fichajeApi.login(userName, password)
    }

    override suspend fun enter(name: String): SigningData {
        return fichajeApi.enter(name)
    }

    override suspend fun exit(name: String): SigningData {
        return fichajeApi.exit(name)
    }

}