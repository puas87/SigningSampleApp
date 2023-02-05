package com.javilena87.fichaje.domain.usecases.login

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import javax.inject.Inject

class GetPasswordUseCase @Inject constructor(
    @PreferencesSource private val fichajeSharedPreferencesRepository: FichajeSharedPrefsRepository
) {

    operator fun invoke(fallback: String = ""): String {
        return fichajeSharedPreferencesRepository.getPassword(fallback)
            .ifEmpty { fallback }
    }
}

fun GetPasswordUseCase.isPasswordDataChanged(value: String): Boolean {
    return this().isNotBlank()
            && value != this()
}