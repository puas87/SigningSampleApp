package com.javilena87.fichaje.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.domain.HolidayRepository
import com.javilena87.fichaje.domain.usecases.alarm.GetAlarmInitTimeUseCase
import com.javilena87.fichaje.domain.usecases.alarm.SetInitTimeUseCase
import com.javilena87.fichaje.receiver.FichajeReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

const val SEVEN_HOURS: Long = 1000 * 60 * 60 * 7
const val TOTAL_FRIDAY_DELAYED_TIME: Long = SEVEN_HOURS
const val FIREBASE_DATABASE_NAME: String = "Festivos"
const val FIREBASE_DATABASE_DAYTYPE_KEY: String = "dayType"
const val FIREBASE_DATABASE_DAYTYPE_VALUE_HOLYDAY: String = "Festivo"
const val FIREBASE_DATABASE_DATE_FORMAT: String = "dd-MM-yyyy"
const val ALARM_SCHEDULED_ACTION_VALUE: String = "SCHEDULED_ACTION"
const val ALARM_DELAYED_ACTION_VALUE: String = "DELAYED_ACTION"

fun setInitialAlarm(scope: CoroutineScope, setInitTime: SetInitTimeUseCase, getAlarmInitTime: GetAlarmInitTimeUseCase, result: (milliseconds: Long) -> Unit) {
    scope.launch {
        result(getAlarmInitTime(setInitTime()))
    }
}

fun initAlarm(context: Context, nextDayForAlarm: Long) {
    val alarmMgr: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent: PendingIntent =
        Intent(context, FichajeReceiver::class.java).let { intent ->
            intent.action = ALARM_SCHEDULED_ACTION_VALUE
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

    setAlarm(alarmMgr, nextDayForAlarm, alarmIntent)
}

fun initDelayedAlarm(context: Context, totalDelay: Long) {
    val alarmMgr: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent: PendingIntent =
        Intent(context, FichajeReceiver::class.java).let { intent ->
            intent.action = ALARM_DELAYED_ACTION_VALUE
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

    setAlarm(alarmMgr, totalDelay, alarmIntent)
}

fun getDaysToAdd(dayOfWeek: Int): Int {
    return when (dayOfWeek) {
        Calendar.FRIDAY -> 3
        Calendar.SATURDAY -> 2
        else -> 1
    }
}

fun isWeekend(dayOfWeek: Int): Boolean {
    return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
}

private fun setAlarm(
    alarmMgr: AlarmManager,
    timer: Long,
    alarmIntent: PendingIntent
) {
    alarmMgr.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timer,
        alarmIntent
    )
}