package com.javilena87.fichaje.domain.usecases.calendar

import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.domain.HolidayRepository
import com.javilena87.fichaje.domain.model.HolidayDataItem
import javax.inject.Inject

class GetAllHolidaysUseCase @Inject constructor(@DatabaseSource private val holidayRepository: HolidayRepository) {

    suspend operator fun invoke(): List<HolidayDataItem.HolidayItem> = holidayRepository.getAllHolidays()
            .map { HolidayDataItem.HolidayItem(it) }
}