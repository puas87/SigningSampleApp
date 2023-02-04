package com.javilena87.fichaje.data.repository

import android.content.SharedPreferences
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository
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
const val DEFAULT_INITIAL_ALARM_HOUR = 8
const val DEFAULT_FINAL_ALARM_HOUR = 17
const val DEFAULT_INITIAL_ALARM_MINUTE = 10
const val DEFAULT_EXIT_ALARM_MINUTE = 30

@Singleton
class FichajeSharedPrefsRepositoryImpl @Inject constructor(
    private val fichajeSecureShared: SharedPreferences
) : FichajeSharedPrefsRepository {

    override fun setUserData(userName: String, password: String) {
        setUserName(userName)
        setPassword(password)
    }

    override fun setUserName(userName: String) {
        with(fichajeSecureShared.edit()) {
            putString(USERNAME_KEY, userName)
            apply()
        }
    }

    override fun setPassword(password: String) {
        with(fichajeSecureShared.edit()) {
            putString(PASSWORD_KEY, password)
            apply()
        }
    }

    override fun getUsername(fallback: String): String {
        return fichajeSecureShared.getString(USERNAME_KEY, fallback)!!
    }

    override fun getPassword(fallback: String): String {
        return fichajeSecureShared.getString(PASSWORD_KEY, fallback)!!
    }

    override fun getAlarmState(): Boolean {
        return fichajeSecureShared.getBoolean(ALARM_STATE_KEY, false)
    }

    override fun setAlarmState(checked: Boolean) {
        with(fichajeSecureShared.edit()) {
            putBoolean(ALARM_STATE_KEY, checked)
            apply()
        }
    }

    override fun setEntryRegister() {
        with(fichajeSecureShared.edit()) {
            putString(LAST_SIGNING_OPERATION_KEY, LAST_SIGNING_OPERATION_ENTRY_VALUE)
            apply()
        }
    }

    override fun setExitRegister() {
        with(fichajeSecureShared.edit()) {
            putString(LAST_SIGNING_OPERATION_KEY, LAST_SIGNING_OPERATION_EXIT_VALUE)
            apply()
        }
    }

    override fun getLastRegister(): String {
        return fichajeSecureShared.getString(LAST_SIGNING_OPERATION_KEY, "")!!
    }

    override fun setAlarmRegister(hour: Int, minute: Int, enter: Boolean) {
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

    override fun getHourAlarm(enter: Boolean): Int {
        return fichajeSecureShared.getInt(getAlarmHourKey(enter), if (enter) DEFAULT_INITIAL_ALARM_HOUR else DEFAULT_FINAL_ALARM_HOUR)
    }

    override fun getMinuteAlarm(enter: Boolean): Int {
        return fichajeSecureShared.getInt(getAlarmMinuteKey(enter), if (enter) DEFAULT_INITIAL_ALARM_MINUTE else DEFAULT_EXIT_ALARM_MINUTE)
    }

}