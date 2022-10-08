package com.javilena87.fichaje.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HolidayRegDao {

    @Query("SELECT * FROM holiday_reg ORDER BY dayIn ASC")
    suspend fun getAllDays(): List<HolidayReg>

    @Query("SELECT * " +
            "FROM holiday_reg " +
            "where :dateInForCheck BETWEEN dayIn AND dayOut " +
            "OR :dateOutForCheck BETWEEN dayIn AND dayOut " +
            "UNION " +
            "SELECT * " +
            "FROM holiday_reg " +
            "WHERE dayIn BETWEEN :dateInForCheck AND :dateOutForCheck " +
            "OR dayOut BETWEEN :dateInForCheck AND :dateOutForCheck")
    suspend fun getDateIsInRange(
        dateInForCheck: Long,
        dateOutForCheck: Long
    ): List<HolidayReg>

    @Query("SELECT * " +
            "FROM holiday_reg " +
            "where :dateInForCheck BETWEEN dayIn AND dayOut")
    suspend fun getDateIsInRange(
        dateInForCheck: Long
    ): List<HolidayReg>

    @Insert
    suspend fun insertRange(holidayRange: HolidayReg)

    @Delete
    suspend fun deleteRange(holidayRange: HolidayReg)
}