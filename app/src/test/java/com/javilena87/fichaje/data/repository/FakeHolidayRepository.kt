package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.domain.HolidayRepository
import java.util.*

class FakeHolidayRepository constructor(private var _listOfMemory: MutableList<HolidayReg> = mutableListOf()) :
    HolidayRepository {

    val listOfMemory = _listOfMemory

    override suspend fun getAllHolidays(): List<HolidayReg> {
        return _listOfMemory
    }

    override suspend fun addDayRangeRegister(holidayRange: HolidayReg) {
        _listOfMemory.add(holidayRange)
    }

    override suspend fun deleteDayRange(holidayRange: HolidayReg) {
        _listOfMemory.remove(holidayRange)
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long, dateOutForCheck: Long): List<HolidayReg> {
        return _listOfMemory.filter { item ->
            (((dateInForCheck >= item.dayIn) && (dateInForCheck <= item.dayOut)) ||
                    ((dateOutForCheck >= item.dayIn) && (dateOutForCheck <= item.dayOut))) ||
                    (((item.dayIn >= dateInForCheck) && (item.dayIn <= dateOutForCheck)) ||
                            ((item.dayOut >= dateInForCheck) && (item.dayOut <= dateOutForCheck)))
        }
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long): List<HolidayReg> {
        return _listOfMemory.filter { item ->
            dateInForCheck > item.dayIn && dateInForCheck < item.dayOut
        }
    }

    override suspend fun getHolidayFromFirebase(calendar: Calendar): NationalHolidaysDatabaseValueResult {
        return NationalHolidaysDatabaseValueResult.Success(calendar.timeInMillis)
    }

}