package com.javilena87.fichaje.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holiday_reg")
data class HolidayReg(
    @PrimaryKey val dayIn: Long,
    val dayOut: Long
)
