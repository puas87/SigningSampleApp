package com.javilena87.fichaje.data

import com.javilena87.fichaje.data.model.SigningData
import com.javilena87.fichaje.data.model.UserData

interface FichajeRepository {
    suspend fun login(userName: String, password: String): UserData

    suspend fun enter(name: String): SigningData

    suspend fun exit(name: String): SigningData
}