package com.javilena87.fichaje.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.data.FichajeRepository
import com.javilena87.fichaje.data.FichajeSharedPrefs
import com.javilena87.fichaje.data.HolidayRepository
import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.di.RemoteSource
import com.javilena87.fichaje.ui.result.model.ResultAlarmState
import com.javilena87.fichaje.ui.result.model.ResultEnterViewState
import com.javilena87.fichaje.ui.result.model.ResultExitViewState
import com.javilena87.fichaje.utils.getDaysToAdd
import com.javilena87.fichaje.utils.isWeekend
import com.javilena87.fichaje.utils.setInitialAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

const val VALID_CHECK_IN = "check in"
const val VALID_CHECK_OUT = "check out"

@HiltViewModel
class ResultViewModel @Inject constructor(
    @RemoteSource private val fichajeRepository: FichajeRepository,
    @DatabaseSource private val holidayRepository: HolidayRepository,
    @PreferencesSource private val fichajeSharedPrefs: FichajeSharedPrefs
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
                val response = fichajeRepository.enter(fichajeSharedPrefs.getUsername())
                fichajeSharedPrefs.setEntryRegister()
                _resultEnterState.value = ResultEnterViewState(VALID_CHECK_IN == response.signing)
            } catch (e: Exception) {
                _resultEnterState.value = ResultEnterViewState()
            }
        }
    }

    fun exit() {
        viewModelScope.launch {
            try {
                val response = fichajeRepository.exit(fichajeSharedPrefs.getUsername())
                fichajeSharedPrefs.setExitRegister()
                _resultExitState.value = ResultExitViewState(VALID_CHECK_OUT == response.signing)
            } catch (e: Exception) {
                _resultExitState.value = ResultExitViewState()
            }
        }
    }

    fun checkAlarm(): Boolean {
        return fichajeSharedPrefs.getAlarmState()
    }

    fun setAlarm(checked: Boolean) {
        _resultAlarmOnOffState.value = checked
        fichajeSharedPrefs.setAlarmState(checked)
    }

    fun enableAlarm() {
        setInitialAlarm(viewModelScope, fichajeSharedPrefs, holidayRepository) {
            _resultAlarmState.value = ResultAlarmState(it)
        }
    }

}