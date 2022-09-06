package com.javilena87.fichaje.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javilena87.fichaje.data.db.firebase.FirebaseHolidaysDatabaseValueResult
import com.javilena87.fichaje.data.prefs.FichajeSharedPrefs
import com.javilena87.fichaje.data.repository.FichajeRepository
import com.javilena87.fichaje.data.repository.HolidayRepository
import com.javilena87.fichaje.ui.result.model.ResultAlarmState
import com.javilena87.fichaje.ui.result.model.ResultEnterViewState
import com.javilena87.fichaje.ui.result.model.ResultExitViewState
import com.javilena87.fichaje.utils.getDaysToAdd
import com.javilena87.fichaje.utils.isWeekend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

const val VALID_CHECK_IN = "check in"
const val VALID_CHECK_OUT = "check out"

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val fichajeRepository: FichajeRepository,
    private val fichajeSharedPrefs: FichajeSharedPrefs,
    private val holidayRepository: HolidayRepository
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

    fun setInitialAlarm() {
        viewModelScope.launch {
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, fichajeSharedPrefs.getHourAlarm(true))
                set(Calendar.MINUTE, fichajeSharedPrefs.getMinuteAlarm(true))
                val currentDay = get(Calendar.DAY_OF_WEEK)
                if (timeInMillis < System.currentTimeMillis() || isWeekend(currentDay)) {
                    add(Calendar.DAY_OF_MONTH, getDaysToAdd(currentDay))
                }
            }
            calendar.timeInMillis = getDayFromDB(calendar)
            _resultAlarmState.value = ResultAlarmState(getDayFromFirebase(calendar))
        }
    }

    private suspend fun getDayFromFirebase(calendar: Calendar): Long {
        when (val result = holidayRepository.getHolidayFromFirebase(calendar)) {
            is FirebaseHolidaysDatabaseValueResult.Success -> {
                return result.validTime
            }
            is FirebaseHolidaysDatabaseValueResult.Error -> {
                return result.currentTime
            }
            is FirebaseHolidaysDatabaseValueResult.NotValid -> {
                calendar.add(Calendar.DAY_OF_MONTH, getDaysToAdd(calendar.get(Calendar.DAY_OF_WEEK)))
                return getDayFromFirebase(calendar)
            }
        }
    }

    private suspend fun getDayFromDB(calendar: Calendar): Long {
        val resultDB = holidayRepository.getDateIsInRange(calendar.timeInMillis).isEmpty()
        return if (resultDB) {
            calendar.timeInMillis
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, getDaysToAdd(calendar.get(Calendar.DAY_OF_WEEK)))
            getDayFromDB(calendar)
        }
    }

}