package com.javilena87.fichaje.presentation.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.domain.usecases.alarm.*
import com.javilena87.fichaje.presentation.result.model.ResultAlarmState
import com.javilena87.fichaje.presentation.result.model.ResultEnterViewState
import com.javilena87.fichaje.presentation.result.model.ResultExitViewState
import com.javilena87.fichaje.utils.setInitialAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val VALID_CHECK_OUT = "check out"

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val setEnter: SetEnterUseCase,
    private val setExit: SetExitUseCase,
    private val setAlarm: SetInitTimeUseCase,
    private val getAlarmInitTime: GetAlarmInitTimeUseCase,
    private val getAlarmState: GetAlarmStateUseCase,
    private val setAlarmState: SetAlarmStateUseCase
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
            _resultEnterState.value = ResultEnterViewState(setEnter())
        }
    }

    fun exit() {
        viewModelScope.launch {
            _resultExitState.value = ResultExitViewState(setExit())
        }
    }

    fun checkAlarm(): Boolean = getAlarmState()

    fun setAlarm(checked: Boolean) {
        _resultAlarmOnOffState.value = checked
        setAlarmState(checked)
    }

    fun enableAlarm() {
        setInitialAlarm(viewModelScope, setAlarm, getAlarmInitTime) {
            _resultAlarmState.value = ResultAlarmState(it)
        }
    }

}