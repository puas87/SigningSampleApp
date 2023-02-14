package com.javilena87.fichaje.domain.usecases.alarm

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.di.RemoteSource
import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import javax.inject.Inject

const val VALID_CHECK_IN = "check in"

class SetEnterUseCase @Inject constructor(
    @RemoteSource private val fichajeRepository: FichajeRepository,
    @PreferencesSource private val fichajeSharedPrefsRepository: FichajeSharedPrefsRepository
) {

    suspend operator fun invoke(): Boolean =
        try {
            val response = fichajeRepository.enter(fichajeSharedPrefsRepository.getUsername())
            fichajeSharedPrefsRepository.setEntryRegister()
            VALID_CHECK_IN == response.signing
        } catch (e: Exception) {
            false
        }
}