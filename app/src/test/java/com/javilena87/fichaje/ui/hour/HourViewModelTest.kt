package com.javilena87.fichaje.ui.hour

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.javilena87.fichaje.data.prefs.FakeFichajeSharedPrefs
import com.javilena87.fichaje.data.repository.DEFAULT_EXIT_ALARM_MINUTE
import com.javilena87.fichaje.data.repository.DEFAULT_FINAL_ALARM_HOUR
import com.javilena87.fichaje.data.repository.DEFAULT_INITIAL_ALARM_HOUR
import com.javilena87.fichaje.data.repository.DEFAULT_INITIAL_ALARM_MINUTE
import com.javilena87.fichaje.getOrAwaitValue
import com.javilena87.fichaje.presentation.hour.HourViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

internal class HourViewModelTest {

    private var fakeSharedPrefs: FakeFichajeSharedPrefs = FakeFichajeSharedPrefs()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `given HourViewModel when init entry timer then set entry timer state`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)

        hourViewModel.initEntryTimer()

        val value = hourViewModel.resultEntryTimerState.getOrAwaitValue()

        assertEquals("Hour is not equal", DEFAULT_INITIAL_ALARM_HOUR, value.hour)
        assertEquals("Minute is not equal", DEFAULT_INITIAL_ALARM_MINUTE, value.minute)
    }

    @Test
    fun `given HourViewModel when init exit timer then set exit timer state`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)

        hourViewModel.initExitTimer()

        val value = hourViewModel.resultExitTimerState.getOrAwaitValue()

        assertEquals("Hour is not equal", DEFAULT_FINAL_ALARM_HOUR, value.hour)
        assertEquals("Minute is not equal", DEFAULT_EXIT_ALARM_MINUTE, value.minute)
    }

    @Test
    fun `given an hour and a minute when set alarm timer for enter then save new data and set enter timer state`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)
        val newHour = 3
        val newMinute = 45

        hourViewModel.setAlarmTimer(newHour, newMinute, true)

        val value = hourViewModel.resultEntryTimerState.getOrAwaitValue()

        assertEquals("Hour is not equal", newHour, value.hour)
        assertEquals("Minute is not equal", newMinute, value.minute)
        assertEquals("Hour saved is not equal", newHour, fakeSharedPrefs.getHourAlarm(true))
        assertEquals("Minute saved is not equal", newMinute, fakeSharedPrefs.getMinuteAlarm(true))
    }

    @Test
    fun `given an hour and a minute when set alarm timer for exit then save new data and set exit timer state`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)
        val newHour = 19
        val newMinute = 37

        hourViewModel.setAlarmTimer(newHour, newMinute, false)

        val value = hourViewModel.resultExitTimerState.getOrAwaitValue()

        assertEquals("Hour is not equal", newHour, value.hour)
        assertEquals("Minute is not equal", newMinute, value.minute)
        assertEquals("Hour saved is not equal", newHour, fakeSharedPrefs.getHourAlarm(false))
        assertEquals("Minute saved is not equal", newMinute, fakeSharedPrefs.getMinuteAlarm(false))
    }

    @Test
    fun `given a new hour and minute when new alarm for entrance then set entry timer button state to false`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)
        val newHour = 19
        val newMinute = 37

        hourViewModel.checkNewEntryAlarm(newHour, newMinute)

        val value = hourViewModel.resultEntryTimerButtonState.getOrAwaitValue()

        assertFalse("Value is not false", value)
    }

    @Test
    fun `given same hour and minute than stored when new alarm for entrance then set entry timer button state to true`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)
        val newHour = DEFAULT_INITIAL_ALARM_HOUR
        val newMinute = DEFAULT_INITIAL_ALARM_MINUTE

        hourViewModel.checkNewEntryAlarm(newHour, newMinute)

        val value = hourViewModel.resultEntryTimerButtonState.getOrAwaitValue()

        assertTrue("Value is not true", value)
    }

    @Test
    fun `given a new hour and minute when new alarm for exit then set exit timer button state to false`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)
        val newHour = 19
        val newMinute = 37

        hourViewModel.checkNewExitAlarm(newHour, newMinute)

        val value = hourViewModel.resultExitTimerButtonState.getOrAwaitValue()

        assertFalse("Value is not false", value)
    }

    @Test
    fun `given same hour and minute than stored when new alarm for exit then set exit timer button state to true`() {
        val hourViewModel = HourViewModel(fakeSharedPrefs)
        val newHour = DEFAULT_FINAL_ALARM_HOUR
        val newMinute = DEFAULT_EXIT_ALARM_MINUTE

        hourViewModel.checkNewExitAlarm(newHour, newMinute)

        val value = hourViewModel.resultExitTimerButtonState.getOrAwaitValue()

        assertTrue("Value is not true", value)
    }
}