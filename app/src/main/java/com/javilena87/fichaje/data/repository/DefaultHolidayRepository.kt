package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.data.DefaultDataSource
import com.javilena87.fichaje.data.HolidayRepository
import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.di.HolidaysSource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultHolidayRepository @Inject constructor(
    @HolidaysSource private val holidayDataSource: DefaultDataSource,
) : HolidayRepository {

    override suspend fun getAllHolidays(): List<HolidayReg> {
        return holidayDataSource.getAllHolidays()
    }

    override suspend fun addDayRangeRegister(holidayRange: HolidayReg) {
        holidayDataSource.addDayRangeRegister(holidayRange)
    }

    override suspend fun deleteDayRange(holidayRange: HolidayReg) {
        holidayDataSource.deleteDayRange(holidayRange)
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long, dateOutForCheck: Long): List<HolidayReg> {
        return holidayDataSource.getDateIsInRange(dateInForCheck, dateOutForCheck)
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long): List<HolidayReg> {
        return holidayDataSource.getDateIsInRange(dateInForCheck)
    }

    override suspend fun getHolidayFromFirebase(calendar: Calendar): NationalHolidaysDatabaseValueResult {
        return holidayDataSource.getNationalHolidays(calendar)
    }

}