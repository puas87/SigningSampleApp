package com.javilena87.fichaje.domain

import com.javilena87.fichaje.domain.model.SigningData
import com.javilena87.fichaje.domain.model.UserData

interface FichajeRepository {
    suspend fun login(userName: String, password: String): UserData

    suspend fun enter(name: String): SigningData

    suspend fun exit(name: String): SigningData
}