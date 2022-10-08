package com.javilena87.fichaje.data

sealed class NationalHolidaysDatabaseValueResult {
    class Success(val validTime: Long) : NationalHolidaysDatabaseValueResult()
    object NotValid :
        NationalHolidaysDatabaseValueResult()

    class Error(val currentTime: Long) : NationalHolidaysDatabaseValueResult()
}