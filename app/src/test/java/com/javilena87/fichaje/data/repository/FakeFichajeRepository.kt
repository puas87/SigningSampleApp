package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.model.SigningData
import com.javilena87.fichaje.domain.model.UserData

class FakeFichajeRepository(var exception: Boolean = false, var requestFailure: Boolean = false) : FichajeRepository {

    override suspend fun login(userName: String, password: String): UserData {
        if (exception) {
            throw Exception("Error retrieving data")
        }
        if (requestFailure) {
            return UserData("", "1234")
        }
        return UserData(userName, "1234")
    }

    override suspend fun enter(name: String): SigningData {
        if (exception) {
            throw Exception("Error retrieving data")
        }
        if (requestFailure) {
            return SigningData("not valid check in", "1234")
        }
        return SigningData("check in", "1234")
    }

    override suspend fun exit(name: String): SigningData {
        if (exception) {
            throw Exception("Error retrieving data")
        }
        if (requestFailure) {
            return SigningData("not valid check out", "1234")
        }
        return SigningData("check out", "1234")
    }
}