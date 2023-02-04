package com.javilena87.fichaje.domain

import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.data.db.HolidayReg
import java.util.*

interface HolidayRepository {
    suspend fun getAllHolidays(): List<HolidayReg>

    suspend fun addDayRangeRegister(holidayRange: HolidayReg)

    suspend fun deleteDayRange(holidayRange: HolidayReg)

    suspend fun getDateIsInRange(dateInForCheck: Long, dateOutForCheck: Long): List<HolidayReg>

    suspend fun getDateIsInRange(dateInForCheck: Long): List<HolidayReg>

    suspend fun getHolidayFromFirebase(calendar: Calendar): NationalHolidaysDatabaseValueResult
}