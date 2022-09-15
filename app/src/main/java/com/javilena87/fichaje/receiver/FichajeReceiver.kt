package com.javilena87.fichaje.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.javilena87.fichaje.data.FichajeRepository
import com.javilena87.fichaje.data.FichajeSharedPrefs
import com.javilena87.fichaje.data.HolidayRepository
import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.data.model.SigningData
import com.javilena87.fichaje.di.DatabaseSource
import com.javilena87.fichaje.di.PreferencesSource
import com.javilena87.fichaje.di.RemoteSource
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
    lateinit var fichajeSharedPrefs: FichajeSharedPrefs

    private val viewModelJob = Job()

    private val corutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            if (fichajeSharedPrefs.getAlarmState()) {
                setInitialAlarm(context)
            }
        }
        if (intent.action == ALARM_DELAYED_ACTION_VALUE) {
            //login and exit
            setInitialAlarm(context)
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
        calendarValue.set(Calendar.HOUR, fichajeSharedPrefs.getHourAlarm(enter))
        calendarValue.set(Calendar.MINUTE, fichajeSharedPrefs.getMinuteAlarm(enter))
        return calendarValue
    }

    fun loginAndEnter(context: Context) {
        corutineScope.launch {
            val result = login()
            if (result) {
                try {
                    val enterResult =
                        getSuccessFromResult(fichajeRepository.enter(fichajeSharedPrefs.getUsername()))
                    if (enterResult) fichajeSharedPrefs.setEntryRegister()
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
                        getSuccessFromResult(fichajeRepository.exit(fichajeSharedPrefs.getUsername()))
                    if (exitResult) fichajeSharedPrefs.setExitRegister()
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
                userName = fichajeSharedPrefs.getUsername(),
                password = fichajeSharedPrefs.getPassword()
            ).name.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private fun getSuccessFromResult(result: SigningData): Boolean {
        return (result.signing.contains("checkIn") || result.signing.contains("checkOut"))
    }


    private fun setInitialAlarm(context: Context) {
        corutineScope.launch {
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
            initAlarm(context, getDayFromFirebase(calendar))
        }
    }

    private suspend fun getDayFromFirebase(calendar: Calendar): Long {
        when (val result = holidayRepository.getHolidayFromFirebase(calendar)) {
            is NationalHolidaysDatabaseValueResult.Success -> {
                return result.validTime
            }
            is NationalHolidaysDatabaseValueResult.Error -> {
                return result.currentTime
            }
            is NationalHolidaysDatabaseValueResult.NotValid -> {
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