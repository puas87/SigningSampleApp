package com.javilena87.fichaje.domain.usecases.alarm

import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.domain.HolidayRepository
import com.javilena87.fichaje.utils.getDaysToAdd
import java.util.Calendar
import javax.inject.Inject

class GetAlarmInitTimeUseCase @Inject constructor(@DatabaseSource private val holidayRepository: HolidayRepository) {

    suspend operator fun invoke(calendar: Calendar): Long {
        calendar.timeInMillis = getDayFromDB(calendar, holidayRepository)
        return getDayFromFirebase(calendar, holidayRepository)
    }

    private suspend fun getDayFromFirebase(calendar: Calendar, holidayRepository: HolidayRepository): Long {
        return when (val result = holidayRepository.getHolidayFromFirebase(calendar)) {
            is NationalHolidaysDatabaseValueResult.Success -> {
                result.validTime
            }
            is NationalHolidaysDatabaseValueResult.Error -> {
                result.currentTime
            }
            is NationalHolidaysDatabaseValueResult.NotValid -> {
                calendar.add(Calendar.DAY_OF_MONTH, getDaysToAdd(calendar.get(Calendar.DAY_OF_WEEK)))
                getDayFromFirebase(calendar, holidayRepository)
            }
        }
    }

    private suspend fun getDayFromDB(calendar: Calendar, holidayRepository: HolidayRepository): Long {
        val resultDB = holidayRepository.getDateIsInRange(calendar.timeInMillis).isEmpty()
        return if (resultDB) {
            calendar.timeInMillis
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, getDaysToAdd(calendar.get(Calendar.DAY_OF_WEEK)))
            getDayFromDB(calendar, holidayRepository)
        }
    }
}