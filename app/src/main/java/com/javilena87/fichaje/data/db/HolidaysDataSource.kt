package com.javilena87.fichaje.data.db

import com.javilena87.fichaje.data.DefaultDataSource
import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.data.db.firebase.FirebaseDataSource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HolidaysDataSource @Inject constructor(
    private val holidayRegDao: HolidayRegDao,
    private val firebaseDataSource: FirebaseDataSource
) : DefaultDataSource {

    override suspend fun getAllHolidays(): List<HolidayReg> {
        return holidayRegDao.getAllDays()
    }

    override suspend fun addDayRangeRegister(holidayRange: HolidayReg) {
        holidayRegDao.insertRange(holidayRange)
    }

    override suspend fun deleteDayRange(holidayRange: HolidayReg) {
        holidayRegDao.deleteRange(holidayRange)
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long, dateOutForCheck: Long): List<HolidayReg> {
        return holidayRegDao.getDateIsInRange(dateInForCheck, dateOutForCheck)
    }

    override suspend fun getDateIsInRange(dateInForCheck: Long): List<HolidayReg> {
        return holidayRegDao.getDateIsInRange(dateInForCheck)
    }

    override suspend fun getNationalHolidays(calendar: Calendar): NationalHolidaysDatabaseValueResult {
        return firebaseDataSource.getHolidayFromFirebase(calendar)
    }
}