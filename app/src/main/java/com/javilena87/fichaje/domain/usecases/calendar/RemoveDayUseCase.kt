package com.javilena87.fichaje.domain.usecases.calendar

import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.domain.HolidayRepository
import javax.inject.Inject

class RemoveDayUseCase @Inject constructor(
    @DatabaseSource private val holidayRepository: HolidayRepository,
    private val getAllHolidays: GetAllHolidaysUseCase
) {

    suspend operator fun invoke(position: Int): HolidayReg {
        val itemToRemove = getAllHolidays()[position]
        holidayRepository.deleteDayRange(itemToRemove.holiday)
        return itemToRemove.holiday
    }
}