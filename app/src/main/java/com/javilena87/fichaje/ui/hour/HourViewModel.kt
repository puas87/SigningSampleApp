package com.javilena87.fichaje.ui.hour

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.javilena87.fichaje.data.FichajeSharedPrefs
import com.javilena87.fichaje.data.prefs.FichajeSharedPrefsImpl
import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.ui.hour.model.HourEntryTimerState
import com.javilena87.fichaje.ui.hour.model.HourExitTimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HourViewModel @Inject constructor(
    @PreferencesSource private val fichajeSharedPrefs: FichajeSharedPrefs
) : ViewModel() {

    private val _resultEntryTimerState = MutableLiveData<HourEntryTimerState>()
    val resultEntryTimerState: LiveData<HourEntryTimerState> = _resultEntryTimerState
    private val _resultExitTimerState = MutableLiveData<HourExitTimerState>()
    val resultExitTimerState: LiveData<HourExitTimerState> = _resultExitTimerState

    private val _resultEntryTimerButtonState = MutableLiveData<Boolean>()
    val resultEntryTimerButtonState: LiveData<Boolean> = _resultEntryTimerButtonState

    private val _resultExitTimerButtonState = MutableLiveData<Boolean>()
    val resultExitTimerButtonState: LiveData<Boolean> = _resultExitTimerButtonState

    fun initEntryTimer() {
        initTimer(true)
    }

    fun initExitTimer() {
        initTimer(false)
    }

    private fun initTimer(enter: Boolean) {
        if (enter) {
            _resultEntryTimerState.value = HourEntryTimerState(fichajeSharedPrefs.getHourAlarm(enter),
                fichajeSharedPrefs.getMinuteAlarm(enter))
        } else {
            _resultExitTimerState.value = HourExitTimerState(fichajeSharedPrefs.getHourAlarm(
                enter),
                fichajeSharedPrefs.getMinuteAlarm(enter))
        }
    }

    fun setAlarmTimer(hourOfDay: Int, minute: Int, enter: Boolean) {
        fichajeSharedPrefs.setAlarmRegister(hourOfDay, minute, enter)
        initTimer(enter)
    }

    fun checkNewEntryAlarm(hourOfDay: Int, minute: Int) {
        val hourRegistered = fichajeSharedPrefs.getHourAlarm(true)
        val minuteRegistered = fichajeSharedPrefs.getMinuteAlarm(true)
        _resultEntryTimerButtonState.value = hourRegistered == hourOfDay && minuteRegistered == minute
    }

    fun checkNewExitAlarm(hourOfDay: Int, minute: Int) {
        val hourRegistered = fichajeSharedPrefs.getHourAlarm(false)
        val minuteRegistered = fichajeSharedPrefs.getMinuteAlarm(false)
        _resultExitTimerButtonState.value = hourRegistered == hourOfDay && minuteRegistered == minute
    }

}