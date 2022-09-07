package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.data.FichajeRepository
import com.javilena87.fichaje.data.model.SigningData
import com.javilena87.fichaje.data.model.UserData
import java.lang.Exception

class FakeFichajeRepository(var errorData: Boolean = false) : FichajeRepository {

    override suspend fun login(userName: String, password: String): UserData {
        if (errorData) {
            throw Exception("Error retrieving data")
        }
        return UserData(userName, "1234")
    }

    override suspend fun enter(name: String): SigningData {
        if (errorData) {
            throw Exception("Error retrieving data")
        }
        return SigningData("check in", "1234")
    }

    override suspend fun exit(name: String): SigningData {
        if (errorData) {
            throw Exception("Error retrieving data")
        }
        return SigningData("check out", "1234")
    }
}