package com.javilena87.fichaje.domain.usecases.login

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import javax.inject.Inject

class ClearDataUseCase @Inject constructor(
    @PreferencesSource private val fichajeSharedPreferencesRepository: FichajeSharedPrefsRepository
) {

    operator fun invoke() {
        fichajeSharedPreferencesRepository.setUserData("", "")
    }
}