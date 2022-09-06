package com.javilena87.fichaje.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

const val FICHAJE_DATABASE_NAME = "fichajeApp-db"

@Database(entities = [HolidayReg::class], version = 1)
abstract class FichajeDatabase : RoomDatabase() {
    abstract fun getHolidayRegDao(): HolidayRegDao
}