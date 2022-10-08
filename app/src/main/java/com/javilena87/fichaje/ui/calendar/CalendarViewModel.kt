package com.javilena87.fichaje.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.data.HolidayRepository
import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.data.repository.HolidayRepositoryImpl
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.ui.calendar.adapter.DataItem
import com.javilena87.fichaje.ui.calendar.model.CalendarHolidaysListState
import com.javilena87.fichaje.ui.calendar.model.CalendarOneDaySelectionState
import com.javilena87.fichaje.ui.calendar.model.CalendarRangeSelectionState
import com.javilena87.fichaje.ui.calendar.model.CalendarRemoveHolidayState
import com.javilena87.fichaje.utils.setEndOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(@DatabaseSource private val holidayRepository: HolidayRepository) :
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
            val isNotInRange = holidayRepository.getDateIsInRange(
                holidayReg.dayIn,
                holidayReg.dayOut
            ).isEmpty()
            if (isNotInRange) {
                holidayRepository.addDayRangeRegister(holidayReg)
                getAllHolidays()
            } else {
                _calendarSelectionErrorState.value = true
            }
        }
    }

    fun getAllHolidays() {
        viewModelScope.launch {
            val holidayList = holidayRepository.getAllHolidays()
                .map { DataItem.HolidayItem(it) }
            _calendarHolidaysListState.value =
                CalendarHolidaysListState(holidayList)
        }
    }

    fun deleteHolidayFromPosition(position: Int) {
        viewModelScope.launch {
            val holidayList = holidayRepository.getAllHolidays()
            val itemToRemove = holidayList[position]
            holidayRepository.deleteDayRange(itemToRemove)
            _calendarRemoveHolidayState.value = CalendarRemoveHolidayState(itemToRemove, position)
            getAllHolidays()
        }
    }
}