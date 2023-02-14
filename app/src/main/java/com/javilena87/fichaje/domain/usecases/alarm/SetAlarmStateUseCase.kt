package com.javilena87.fichaje.domain.usecases.alarm

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import javax.inject.Inject

class SetAlarmStateUseCase @Inject constructor(
    @PreferencesSource private val fichajeSharedPrefsRepository: FichajeSharedPrefsRepository
) {

    operator fun invoke(checked: Boolean) {
        fichajeSharedPrefsRepository.setAlarmState(checked)
    }
}