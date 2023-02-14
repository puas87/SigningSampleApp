package com.javilena87.fichaje.ui.calendar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.javilena87.fichaje.MainCoroutineRule
import com.javilena87.fichaje.data.db.HolidayReg
import com.javilena87.fichaje.data.repository.FakeHolidayRepository
import com.javilena87.fichaje.domain.usecases.calendar.GetAllHolidaysUseCase
import com.javilena87.fichaje.domain.usecases.calendar.RemoveDayUseCase
import com.javilena87.fichaje.domain.usecases.calendar.SetCalendarSelectionUseCase
import com.javilena87.fichaje.getOrAwaitValue
import com.javilena87.fichaje.presentation.calendar.CalendarViewModel
import com.javilena87.fichaje.utils.setEndOfDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.*

internal class CalendarViewModelTest {

    private var fakeHolidayRepository: FakeHolidayRepository = FakeHolidayRepository()

    private val setCalendarSelection = SetCalendarSelectionUseCase(fakeHolidayRepository)

    private val getAllHolidays = GetAllHolidaysUseCase(fakeHolidayRepository)

    private val removeDayUseCase = RemoveDayUseCase(fakeHolidayRepository, getAllHolidays)

    private val calendarViewModel = CalendarViewModel(
        setCalendarSelection,
        getAllHolidays,
        removeDayUseCase)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `given CalendarViewModel when enable one day click calendar then notify states`() {
        calendarViewModel.enableDayClickCalendar()

        val valueOneDay = calendarViewModel.calendarOneDaySelectionState.getOrAwaitValue()
        val valueRange = calendarViewModel.calendarRangeSelectionState.getOrAwaitValue()

        assertTrue("Value one day is not true", valueOneDay.enabled)
        assertFalse("Value range is not false", valueRange.enabled)
    }

    @Test
    fun `given CalendarViewModel when enable range click calendar then notify states`() {
        calendarViewModel.enableRangeClickCalendar()

        val valueOneDay = calendarViewModel.calendarOneDaySelectionState.getOrAwaitValue()
        val valueRange = calendarViewModel.calendarRangeSelectionState.getOrAwaitValue()

        assertTrue("Value range is not true", valueRange.enabled)
        assertFalse("Value one day is not false", valueOneDay.enabled)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given and long for init and long for end when add holiday selection then set new value`() = runTest {
        val calendar = Calendar.getInstance()
        val calendarOut = Calendar.getInstance().apply {
            setEndOfDay()
        }

        calendarViewModel.addHolidaysSelection(calendar.timeInMillis, calendar.timeInMillis)

        val allRegisters = fakeHolidayRepository.getAllHolidays()

        assertTrue("List of registers is empty", allRegisters.isNotEmpty())
        assertThat(allRegisters[0], `is`(HolidayReg(calendar.timeInMillis, calendarOut.timeInMillis)))
    }

    @Test
    fun `given and long for init and long for end when add holiday selection then send new list`() {
        val calendar = Calendar.getInstance()

        calendarViewModel.addHolidaysSelection(calendar.timeInMillis, calendar.timeInMillis)

        val allRegisters = calendarViewModel.calendarHolidaysListState.getOrAwaitValue()

        assertThat(allRegisters.holidaysList, notNullValue())
        assertTrue("List of registers is empty", allRegisters.holidaysList!!.isNotEmpty())
    }

    @Test
    fun `given CalendarViewModel without holidays stored when get all stored holidays then the list is empty`() {
        calendarViewModel.getAllDaysHolidays()

        val allRegisters = calendarViewModel.calendarHolidaysListState.getOrAwaitValue()

        assertThat(allRegisters.holidaysList, equalTo(emptyList()))
    }


    @Test
    fun `given CalendarViewModel with holidays stored when get all stored holidays then the list is empty`() {
        val calendar = Calendar.getInstance()
        calendarViewModel.addHolidaysSelection(calendar.timeInMillis)

        calendarViewModel.getAllDaysHolidays()

        val allRegisters = calendarViewModel.calendarHolidaysListState.getOrAwaitValue()

        assertTrue("List of registers is empty", allRegisters.holidaysList!!.isNotEmpty())
    }


    @Test
    fun `given two identical holidays register when add second register holidays then notify incorrect register`() {
        val calendar = Calendar.getInstance()
        val calendarOut = Calendar.getInstance().apply {
            setEndOfDay()
        }
        val initialReg = HolidayReg(calendar.timeInMillis, calendarOut.timeInMillis)
        fakeHolidayRepository.listOfMemory.add(initialReg)

        calendarViewModel.addHolidaysSelection(calendar.timeInMillis, calendar.timeInMillis)

        val errorOnRegisterValue = calendarViewModel.calendarSelectionErrorState.getOrAwaitValue()

        assertTrue("Error is not true", errorOnRegisterValue)
    }

    @Test
    fun `given two identical holidays register when add second register holidays then not add to the list`() {
        val calendar = Calendar.getInstance()
        val calendarOut = Calendar.getInstance().apply {
            setEndOfDay()
        }
        val initialReg = HolidayReg(calendar.timeInMillis, calendarOut.timeInMillis)
        fakeHolidayRepository.listOfMemory.add(initialReg)

        calendarViewModel.addHolidaysSelection(calendar.timeInMillis, calendar.timeInMillis)

        calendarViewModel.getAllDaysHolidays()
        val allRegisters = calendarViewModel.calendarHolidaysListState.getOrAwaitValue()

        assertTrue("List of registers is empty", allRegisters.holidaysList!!.size == 1)
    }

    @Test
    fun `given a previous register when delete it then list has to be empty`() {
        val calendar = Calendar.getInstance()
        val calendarOut = Calendar.getInstance().apply {
            setEndOfDay()
        }
        val initialReg = HolidayReg(calendar.timeInMillis, calendarOut.timeInMillis)
        fakeHolidayRepository.listOfMemory.add(initialReg)

        calendarViewModel.deleteHolidayFromPosition(0)

        val allRegisters = calendarViewModel.calendarHolidaysListState.getOrAwaitValue()

        assertTrue("List of registers is empty", allRegisters.holidaysList!!.isEmpty())
    }

    @Test
    fun `given a previous register when delete it then notify the item removed`() {
        val calendar = Calendar.getInstance()
        val calendarOut = Calendar.getInstance().apply {
            setEndOfDay()
        }
        val initialReg = HolidayReg(calendar.timeInMillis, calendarOut.timeInMillis)
        fakeHolidayRepository.listOfMemory.add(initialReg)

        calendarViewModel.deleteHolidayFromPosition(0)

        val itemRemoved = calendarViewModel.calendarRemoveHolidayState.getOrAwaitValue()

        assertEquals("Item is not equals", initialReg, itemRemoved.holidayItemRemoved)
        assertTrue("Position is not the same", itemRemoved.position == 0)
    }
}