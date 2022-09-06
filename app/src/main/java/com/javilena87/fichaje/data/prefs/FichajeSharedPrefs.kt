package com.javilena87.fichaje.data.prefs

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

const val ALARM_STATE_KEY = "ALARM_STATE"
const val LAST_SIGNING_OPERATION_KEY = "LAST_SIGNING_OPERATION_KEY"
const val INITIAL_ALARM_HOUR_KEY = "INITIAL_ALARM_HOUR_KEY"
const val INITIAL_ALARM_MINUTE_KEY = "INITIAL_ALARM_MINUTE_KEY"
const val END_ALARM_HOUR_KEY = "END_ALARM_HOUR_KEY"
const val END_ALARM_MINUTE_KEY = "END_ALARM_MINUTE_KEY"
const val LAST_SIGNING_OPERATION_ENTRY_VALUE = "ENTRY"
const val LAST_SIGNING_OPERATION_EXIT_VALUE = "EXIT"
const val USERNAME_KEY = "signing_username"
const val PASSWORD_KEY = "signing_password"

@Singleton
class FichajeSharedPrefs @Inject constructor(
    private val fichajeSecureShared: SharedPreferences
){

    fun setUserData(userName: String, password: String) {
        setUserName(userName)
        setPassword(password)
    }

    fun setUserName(userName: String) {
        with(fichajeSecureShared.edit()) {
            putString(USERNAME_KEY, userName)
            apply()
        }
    }

    fun setPassword(password: String) {
        with(fichajeSecureShared.edit()) {
            putString(PASSWORD_KEY, password)
            apply()
        }
    }

    fun getUsername(fallback: String = ""): String {
        return fichajeSecureShared.getString(USERNAME_KEY, fallback)!!
    }

    fun getPassword(fallback: String = ""): String {
        return fichajeSecureShared.getString(PASSWORD_KEY, fallback)!!
    }

    fun getAlarmState(): Boolean {
        return fichajeSecureShared.getBoolean(ALARM_STATE_KEY, false)
    }

    fun setAlarmState(checked: Boolean) {
        with(fichajeSecureShared.edit()) {
            putBoolean(ALARM_STATE_KEY, checked)
            apply()
        }
    }

    fun setEntryRegister() {
        with(fichajeSecureShared.edit()) {
            putString(LAST_SIGNING_OPERATION_KEY, LAST_SIGNING_OPERATION_ENTRY_VALUE)
            apply()
        }
    }

    fun setExitRegister() {
        with(fichajeSecureShared.edit()) {
            putString(LAST_SIGNING_OPERATION_KEY, LAST_SIGNING_OPERATION_EXIT_VALUE)
            apply()
        }
    }

    fun setAlarmRegister(hour: Int, minute: Int, enter: Boolean) {
        with(fichajeSecureShared.edit()) {
            putInt(getAlarmHourKey(enter), hour)
            putInt(getAlarmMinuteKey(enter), minute)
            apply()
        }
    }

    private fun getAlarmMinuteKey(enter: Boolean): String {
        return if (enter) INITIAL_ALARM_MINUTE_KEY else END_ALARM_MINUTE_KEY
    }

    private fun getAlarmHourKey(enter: Boolean): String {
        return if (enter) INITIAL_ALARM_HOUR_KEY else END_ALARM_HOUR_KEY
    }

    fun getHourAlarm(enter: Boolean): Int {
        return fichajeSecureShared.getInt(getAlarmHourKey(enter), if (enter) 8 else 17)
    }

    fun getMinuteAlarm(enter: Boolean): Int {
        return fichajeSecureShared.getInt(getAlarmMinuteKey(enter), if (enter) 10 else 30)
    }

}