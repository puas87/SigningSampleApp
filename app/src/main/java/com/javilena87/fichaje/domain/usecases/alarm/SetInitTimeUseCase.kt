package com.javilena87.fichaje.domain.usecases.alarm

import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import com.javilena87.fichaje.utils.getDaysToAdd
import com.javilena87.fichaje.utils.isWeekend
import java.util.Calendar
import javax.inject.Inject

class SetInitTimeUseCase @Inject constructor(
    @PreferencesSource private val fichajeSharedPrefsRepository: FichajeSharedPrefsRepository
) {

    operator fun invoke(): Calendar =
        Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, fichajeSharedPrefsRepository.getHourAlarm(true))
            set(Calendar.MINUTE, fichajeSharedPrefsRepository.getMinuteAlarm(true))
            val currentDay = get(Calendar.DAY_OF_WEEK)
            if (timeInMillis < System.currentTimeMillis() || isWeekend(currentDay)) {
                add(Calendar.DAY_OF_MONTH, getDaysToAdd(currentDay))
            }
        }
}