package com.javilena87.fichaje.presentation.calendar.model

import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.presentation.calendar.adapter.DataItem

data class CalendarRemoveHolidayState(
    val holidayItemRemoved: HolidayReg?,
    val position: Int = -1
)

data class CalendarOneDaySelectionState(val enabled: Boolean = false)

data class CalendarRangeSelectionState(val enabled: Boolean = false)

data class CalendarHolidaysListState(val holidaysList: List<DataItem>? = null)

