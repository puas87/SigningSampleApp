package com.javilena87.fichaje.presentation.hour

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import com.javilena87.fichaje.presentation.hour.model.HourEntryTimerState
import com.javilena87.fichaje.presentation.hour.model.HourExitTimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HourViewModel @Inject constructor(
    @PreferencesSource private val fichajeSharedPrefsRepository: FichajeSharedPrefsRepository
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
        val hourAlarm = fichajeSharedPrefsRepository.getHourAlarm(enter)
        val minuteAlarm = fichajeSharedPrefsRepository.getMinuteAlarm(enter)
        if (enter) {
            _resultEntryTimerState.value = HourEntryTimerState(hourAlarm,
                minuteAlarm)
        } else {
            _resultExitTimerState.value = HourExitTimerState(hourAlarm,
                minuteAlarm)
        }
    }

    fun setAlarmTimer(hourOfDay: Int, minute: Int, enter: Boolean) {
        fichajeSharedPrefsRepository.setAlarmRegister(hourOfDay, minute, enter)
        initTimer(enter)
    }

    fun checkNewEntryAlarm(hourOfDay: Int, minute: Int) {
        val hourRegistered = fichajeSharedPrefsRepository.getHourAlarm(true)
        val minuteRegistered = fichajeSharedPrefsRepository.getMinuteAlarm(true)
        _resultEntryTimerButtonState.value = hourRegistered == hourOfDay && minuteRegistered == minute
    }

    fun checkNewExitAlarm(hourOfDay: Int, minute: Int) {
        val hourRegistered = fichajeSharedPrefsRepository.getHourAlarm(false)
        val minuteRegistered = fichajeSharedPrefsRepository.getMinuteAlarm(false)
        _resultExitTimerButtonState.value = hourRegistered == hourOfDay && minuteRegistered == minute
    }

}