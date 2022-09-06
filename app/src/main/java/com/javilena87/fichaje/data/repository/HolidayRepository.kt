package com.javilena87.fichaje.data.repository

import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.data.db.HolidayRegDao
import com.javilena87.fichaje.data.db.firebase.FirebaseDataSource
import com.javilena87.fichaje.data.db.firebase.FirebaseHolidaysDatabaseValueResult
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HolidayRepository @Inject constructor(
    private val holidayRegDao: HolidayRegDao,
    private val firebaseDataSource: FirebaseDataSource
) {

    suspend fun getAllHolidays(): List<HolidayReg> {
        return holidayRegDao.getAllDays()
    }

    suspend fun addDayRangeRegister(holidayRange: HolidayReg) {
        holidayRegDao.insertRange(holidayRange)
    }

    suspend fun deleteDayRange(holidayRange: HolidayReg) {
        holidayRegDao.deleteRange(holidayRange)
    }

    suspend fun getDateIsInRange(dateInForCheck: Long, dateOutForCheck: Long): List<HolidayReg> {
        return holidayRegDao.getDateIsInRange(dateInForCheck, dateOutForCheck)
    }

    suspend fun getDateIsInRange(dateInForCheck: Long): List<HolidayReg> {
        return holidayRegDao.getDateIsInRange(dateInForCheck)
    }

    suspend fun getHolidayFromFirebase(calendar: Calendar): FirebaseHolidaysDatabaseValueResult {
        return firebaseDataSource.getHolidayFromFirebase(calendar)
    }

}