package com.javilena87.fichaje.presentation.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import com.javilena87.fichaje.domain.HolidayRepository
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.di.RemoteSource
import com.javilena87.fichaje.presentation.result.model.ResultAlarmState
import com.javilena87.fichaje.presentation.result.model.ResultEnterViewState
import com.javilena87.fichaje.presentation.result.model.ResultExitViewState
import com.javilena87.fichaje.utils.setInitialAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val VALID_CHECK_IN = "check in"
const val VALID_CHECK_OUT = "check out"

@HiltViewModel
class ResultViewModel @Inject constructor(
    @RemoteSource private val fichajeRepository: FichajeRepository,
    @DatabaseSource private val holidayRepository: HolidayRepository,
    @PreferencesSource private val fichajeSharedPrefsRepository: FichajeSharedPrefsRepository
) :
    ViewModel() {

    private val _resultEnterState = MutableLiveData<ResultEnterViewState>()
    val resultEnterState: LiveData<ResultEnterViewState> = _resultEnterState

    private val _resultExitState = MutableLiveData<ResultExitViewState>()
    val resultExitState: LiveData<ResultExitViewState> = _resultExitState

    private val _resultAlarmState = MutableLiveData<ResultAlarmState>()
    val resultAlarmState: LiveData<ResultAlarmState> = _resultAlarmState

    private val _resultAlarmOnOffState = MutableLiveData<Boolean>()
    val resultAlarmOnOffState: LiveData<Boolean> = _resultAlarmOnOffState

    fun enter() {
        viewModelScope.launch {
            try {
                val response = fichajeRepository.enter(fichajeSharedPrefsRepository.getUsername())
                fichajeSharedPrefsRepository.setEntryRegister()
                _resultEnterState.value = ResultEnterViewState(VALID_CHECK_IN == response.signing)
            } catch (e: Exception) {
                _resultEnterState.value = ResultEnterViewState()
            }
        }
    }

    fun exit() {
        viewModelScope.launch {
            try {
                val response = fichajeRepository.exit(fichajeSharedPrefsRepository.getUsername())
                fichajeSharedPrefsRepository.setExitRegister()
                _resultExitState.value = ResultExitViewState(VALID_CHECK_OUT == response.signing)
            } catch (e: Exception) {
                _resultExitState.value = ResultExitViewState()
            }
        }
    }

    fun checkAlarm(): Boolean {
        return fichajeSharedPrefsRepository.getAlarmState()
    }

    fun setAlarm(checked: Boolean) {
        _resultAlarmOnOffState.value = checked
        fichajeSharedPrefsRepository.setAlarmState(checked)
    }

    fun enableAlarm() {
        setInitialAlarm(viewModelScope, fichajeSharedPrefsRepository, holidayRepository) {
            _resultAlarmState.value = ResultAlarmState(it)
        }
    }

}