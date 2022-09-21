package com.javilena87.fichaje.data.repository

import androidx.room.Query
import com.javilena87.fichaje.data.HolidayRepository
import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.data.db.HolidayReg
import java.util.*

class FakeHolidayRepository constructor(private var listOfMemory: MutableList<HolidayReg> = mutableListOf()) :
    HolidayRepository {

    override suspend fun getAllHolidays(): List<HolidayReg> {
        return listOfMemory
    }

    override suspend fun addDayRangeRegister(holidayRange: HolidayReg) {
        listOfMemory.add(holidayRange)
    }

    override suspend fun deleteDayRange(holidayRange: HolidayReg) {
        listOfMemory.remove(holidayRange)
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long, dateOutForCheck: Long): List<HolidayReg> {
        return listOfMemory.filter { item ->
            (((dateInForCheck > item.dayIn) && (dateInForCheck < item.dayOut)) ||
                    ((dateOutForCheck > item.dayIn) && (dateOutForCheck < item.dayOut))) ||
                    (((item.dayIn > dateInForCheck) && (item.dayIn < dateOutForCheck)) ||
                            ((item.dayOut > dateInForCheck) && (item.dayOut < dateOutForCheck)))
        }
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long): List<HolidayReg> {
        return listOfMemory.filter { item ->
            dateInForCheck > item.dayIn && dateInForCheck < item.dayOut
        }
    }

    override suspend fun getHolidayFromFirebase(calendar: Calendar): NationalHolidaysDatabaseValueResult {
        return NationalHolidaysDatabaseValueResult.Success(calendar.timeInMillis)
    }

}