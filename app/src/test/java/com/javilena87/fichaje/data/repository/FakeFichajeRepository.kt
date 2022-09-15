package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.data.FichajeRepository
import com.javilena87.fichaje.data.model.SigningData
import com.javilena87.fichaje.data.model.UserData

class FakeFichajeRepository(var exception: Boolean = false, var userEmpty: Boolean = false) : FichajeRepository {

    override suspend fun login(userName: String, password: String): UserData {
        if (exception) {
            throw Exception("Error retrieving data")
        }
        if (userEmpty) {
            return UserData("", "1234")
        }
        return UserData(userName, "1234")
    }

    override suspend fun enter(name: String): SigningData {
        if (exception) {
            throw Exception("Error retrieving data")
        }
        return SigningData("check in", "1234")
    }

    override suspend fun exit(name: String): SigningData {
        if (exception) {
            throw Exception("Error retrieving data")
        }
        return SigningData("check out", "1234")
    }
}