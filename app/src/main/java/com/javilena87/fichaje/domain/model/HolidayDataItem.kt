package com.javilena87.fichaje.domain.model

import com.javilena87.fichaje.data.db.HolidayReg

sealed class HolidayDataItem {
    data class HolidayItem(val holiday: HolidayReg) : HolidayDataItem() {
        override val id = holiday.dayIn
    }

    abstract val id: Long
}