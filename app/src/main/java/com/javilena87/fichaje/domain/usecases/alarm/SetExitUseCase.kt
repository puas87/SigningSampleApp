package com.javilena87.fichaje.domain.usecases.alarm

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.di.RemoteSource
import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import com.javilena87.fichaje.presentation.result.VALID_CHECK_OUT
import javax.inject.Inject

class SetExitUseCase @Inject constructor(
    @RemoteSource private val fichajeRepository: FichajeRepository,
    @PreferencesSource private val fichajeSharedPrefsRepository: FichajeSharedPrefsRepository
) {

    suspend operator fun invoke(): Boolean =
        try {
            val response = fichajeRepository.exit(fichajeSharedPrefsRepository.getUsername())
            fichajeSharedPrefsRepository.setExitRegister()
            VALID_CHECK_OUT == response.signing
        } catch (e: Exception) {
            false
        }
}