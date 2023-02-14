package com.javilena87.fichaje.presentation.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.domain.usecases.calendar.GetAllHolidaysUseCase
import com.javilena87.fichaje.domain.usecases.calendar.RemoveDayUseCase
import com.javilena87.fichaje.domain.usecases.calendar.SetCalendarSelectionUseCase
import com.javilena87.fichaje.presentation.calendar.model.CalendarHolidaysListState
import com.javilena87.fichaje.presentation.calendar.model.CalendarOneDaySelectionState
import com.javilena87.fichaje.presentation.calendar.model.CalendarRangeSelectionState
import com.javilena87.fichaje.presentation.calendar.model.CalendarRemoveHolidayState
import com.javilena87.fichaje.utils.setEndOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val setCalendarSelection: SetCalendarSelectionUseCase,
    private val getAllHolidays: GetAllHolidaysUseCase,
    private val removeDay: RemoveDayUseCase
) :
    ViewModel() {

    private val _calendarHolidaysListState =
        MutableLiveData<CalendarHolidaysListState>()
    val calendarHolidaysListState: LiveData<CalendarHolidaysListState> =
        _calendarHolidaysListState

    private val _calendarRemoveHolidayState =
        MutableLiveData<CalendarRemoveHolidayState>()
    val calendarRemoveHolidayState: LiveData<CalendarRemoveHolidayState> =
        _calendarRemoveHolidayState

    private val _calendarOneDaySelectionState =
        MutableLiveData<CalendarOneDaySelectionState>()
    val calendarOneDaySelectionState: LiveData<CalendarOneDaySelectionState> =
        _calendarOneDaySelectionState

    private val _calendarRangeSelectionState =
        MutableLiveData<CalendarRangeSelectionState>()
    val calendarRangeSelectionState: LiveData<CalendarRangeSelectionState> =
        _calendarRangeSelectionState

    private val _calendarSelectionErrorState =
        MutableLiveData<Boolean>()
    val calendarSelectionErrorState: LiveData<Boolean> =
        _calendarSelectionErrorState

    fun enableDayClickCalendar() {
        _calendarOneDaySelectionState.value = CalendarOneDaySelectionState(true)
        _calendarRangeSelectionState.value = CalendarRangeSelectionState(false)
    }

    fun enableRangeClickCalendar() {
        _calendarOneDaySelectionState.value =
            CalendarOneDaySelectionState(false)
        _calendarRangeSelectionState.value = CalendarRangeSelectionState(true)
    }

    fun addHolidaysSelection(
        selectionIn: Long,
        selectionOut: Long = selectionIn
    ) {
        val calendarOut = Calendar.getInstance()
        calendarOut.timeInMillis = selectionOut
        calendarOut.setEndOfDay()
        val holidayReg = HolidayReg(selectionIn, calendarOut.timeInMillis)
        addHolidaysSelection(holidayReg)
    }

    fun addHolidaysSelection(holidayReg: HolidayReg) {
        viewModelScope.launch {
            val error = setCalendarSelection(holidayReg)
            if (error) {
                _calendarSelectionErrorState.value = true
            } else {
                getAllDaysHolidays()
            }
        }
    }

    fun getAllDaysHolidays() {
        viewModelScope.launch {
            _calendarHolidaysListState.value =
                CalendarHolidaysListState(getAllHolidays())
        }
    }

    fun deleteHolidayFromPosition(position: Int) {
        viewModelScope.launch {
            _calendarRemoveHolidayState.value = CalendarRemoveHolidayState(removeDay(position), position)
            getAllDaysHolidays()
        }
    }
}