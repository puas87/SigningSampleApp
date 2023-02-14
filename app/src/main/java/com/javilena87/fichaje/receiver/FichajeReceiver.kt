package com.javilena87.fichaje.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.di.RemoteSource
import com.javilena87.fichaje.domain.FichajeRepository
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
import com.javilena87.fichaje.domain.HolidayRepository
import com.javilena87.fichaje.domain.model.SigningData
import com.javilena87.fichaje.domain.usecases.alarm.GetAlarmInitTimeUseCase
import com.javilena87.fichaje.domain.usecases.alarm.SetInitTimeUseCase
import com.javilena87.fichaje.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

const val CHANNEL_ID = "fichaje_channel_id"

@AndroidEntryPoint
class FichajeReceiver : BroadcastReceiver() {

    @RemoteSource
    @Inject
    lateinit var fichajeRepository: FichajeRepository

    @DatabaseSource
    @Inject
    lateinit var holidayRepository: HolidayRepository

    @PreferencesSource
    @Inject
    lateinit var fichajeSharedPrefsRepository: FichajeSharedPrefsRepository

    @Inject
    lateinit var setAlarm: SetInitTimeUseCase

    @Inject
    lateinit var getAlarmInitTime: GetAlarmInitTimeUseCase

    private val viewModelJob = Job()

    private val corutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            if (fichajeSharedPrefsRepository.getAlarmState()) {
                enableAlarm(context)
            }
        }
        if (intent.action == ALARM_DELAYED_ACTION_VALUE) {
            //login and exit
            enableAlarm(context)
            loginAndExit(context)
        }
        if (intent.action == ALARM_SCHEDULED_ACTION_VALUE) {
            //login and enter
            initDelayedAlarm(context, getDelayedTime())
            loginAndEnter(context)
        }
        if (intent.action == NOTIFICATION_ENTRANCE_FAILURE) {
            //login and enter
            cancelNotification(intent, context)
            loginAndEnter(context)
        }
        if (intent.action == NOTIFICATION_EXIT_FAILURE) {
            //login and enter
            cancelNotification(intent, context)
            loginAndExit(context)
        }
    }

    private fun getDelayedTime(): Long {
        return System.currentTimeMillis() +
                if (Calendar.getInstance()
                        .get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
                ) {
                    TOTAL_FRIDAY_DELAYED_TIME
                } else {
                    val initialTime = setTimerValue(true)
                    val endTime = setTimerValue(false)
                    endTime.timeInMillis - initialTime.timeInMillis
                }
    }

    private fun setTimerValue(enter: Boolean): Calendar {
        val calendarValue = Calendar.getInstance()
        calendarValue.set(Calendar.HOUR, fichajeSharedPrefsRepository.getHourAlarm(enter))
        calendarValue.set(Calendar.MINUTE, fichajeSharedPrefsRepository.getMinuteAlarm(enter))
        return calendarValue
    }

    fun loginAndEnter(context: Context) {
        corutineScope.launch {
            val result = login()
            if (result) {
                try {
                    val enterResult =
                        getSuccessFromResult(fichajeRepository.enter(fichajeSharedPrefsRepository.getUsername()))
                    if (enterResult) fichajeSharedPrefsRepository.setEntryRegister()
                    createNotification(
                        context,
                        enter = true,
                        result = enterResult
                    )
                } catch (e: Exception) {
                    createNotification(context, enter = true, result = false)
                }
            } else {
                createNotification(context, enter = true, result = false)
            }
        }
    }

    fun loginAndExit(context: Context) {
        corutineScope.launch {
            val result = login()
            if (result) {
                try {
                    val exitResult =
                        getSuccessFromResult(fichajeRepository.exit(fichajeSharedPrefsRepository.getUsername()))
                    if (exitResult) fichajeSharedPrefsRepository.setExitRegister()
                    createNotification(
                        context,
                        enter = false,
                        result = exitResult
                    )
                } catch (e: Exception) {
                    createNotification(context, enter = false, result = false)
                }
            } else {
                createNotification(context, enter = false, result = false)
            }
        }
    }

    private suspend fun login(): Boolean {
        return try {
            fichajeRepository.login(
                userName = fichajeSharedPrefsRepository.getUsername(),
                password = fichajeSharedPrefsRepository.getPassword()
            ).name.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private fun getSuccessFromResult(result: SigningData): Boolean {
        return (result.signing.contains("checkIn") || result.signing.contains("checkOut"))
    }


    private fun enableAlarm(context: Context) {
        setInitialAlarm(corutineScope, setAlarm, getAlarmInitTime) {
            initAlarm(context, it)
        }
    }

}