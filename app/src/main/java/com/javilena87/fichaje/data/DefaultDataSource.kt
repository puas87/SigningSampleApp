package com.javilena87.fichaje.data

import com.javilena87.fichaje.data.db.HolidayReg
import java.util.*

interface DefaultDataSource {

    suspend fun getAllHolidays(): List<HolidayReg>

    suspend fun addDayRangeRegister(holidayRange: HolidayReg)

    suspend fun deleteDayRange(holidayRange: HolidayReg)

    suspend fun getDateIsInRange(dateInForCheck: Long, dateOutForCheck: Long): List<HolidayReg>

    suspend fun getDateIsInRange(dateInForCheck: Long): List<HolidayReg>

    suspend fun getNationalHolidays(calendar: Calendar): NationalHolidaysDatabaseValueResult

}