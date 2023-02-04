package com.javilena87.fichaje.domain.usecases

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import javax.inject.Inject

class GetUserRememberedUseCase @Inject constructor(
    @PreferencesSource private val fichajeSharedPreferencesRepository: FichajeSharedPrefsRepository
) {

    operator fun invoke(): Boolean {
        return getUserName().isNotBlank()
    }

    private fun getUserName(fallback: String = ""): String {
        return fichajeSharedPreferencesRepository.getUsername(fallback)
            .ifEmpty { fallback }
    }
}