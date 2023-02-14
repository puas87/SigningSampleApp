package com.javilena87.fichaje.domain.usecases.calendar

import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.domain.HolidayRepository
import javax.inject.Inject

class SetCalendarSelectionUseCase @Inject constructor(
    @DatabaseSource private val holidayRepository: HolidayRepository
) {

    suspend operator fun invoke(holidayReg: HolidayReg): Boolean {
        val isNotInRange = holidayRepository.getDateIsInRange(
            holidayReg.dayIn,
            holidayReg.dayOut
        ).isEmpty()
        if (isNotInRange) {
            holidayRepository.addDayRangeRegister(holidayReg)
        } else {
            return true
        }
        return false
    }
}