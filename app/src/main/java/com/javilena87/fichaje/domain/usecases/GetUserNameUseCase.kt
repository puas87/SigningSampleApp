package com.javilena87.fichaje.domain.usecases

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    @PreferencesSource private val fichajeSharedPreferencesRepository: FichajeSharedPrefsRepository
) {

    operator fun invoke(fallback: String = ""): String {
        return fichajeSharedPreferencesRepository.getUsername(fallback)
            .ifEmpty { fallback }
    }

}