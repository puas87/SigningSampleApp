package com.javilena87.fichaje.data.prefs

import com.javilena87.fichaje.data.repository.*
import com.javilena87.fichaje.domain.FichajeSharedPrefsRepository

class FakeFichajeSharedPrefs constructor(private var listOfMemory: MutableMap<String, String?> = mutableMapOf()) :
    FichajeSharedPrefsRepository {
    override fun setUserData(userName: String, password: String) {
        setUserName(userName)
        setPassword(password)
    }

    override fun setUserName(userName: String) {
        listOfMemory["userName"] = userName
    }

    override fun setPassword(password: String) {
        listOfMemory["password"] = password
    }

    override fun getUsername(fallback: String): String {
        return listOfMemory.getOrDefault("userName", fallback)!!
    }

    override fun getPassword(fallback: String): String {
        return listOfMemory.getOrDefault("password", fallback)!!
    }

    override fun getAlarmState(): Boolean {
        return listOfMemory.getOrDefault("alarmState", "false").toBoolean()
    }

    override fun setAlarmState(checked: Boolean) {
        listOfMemory["alarmState"] = checked.toString()
    }

    override fun setEntryRegister() {
        listOfMemory["LAST_SIGNING_OPERATION_KEY"] = "ENTRY"
    }

    override fun setExitRegister() {
        listOfMemory["LAST_SIGNING_OPERATION_KEY"] = "EXIT"
    }

    override fun getLastRegister(): String {
        return listOfMemory["LAST_SIGNING_OPERATION_KEY"]!!
    }

    override fun setAlarmRegister(hour: Int, minute: Int, enter: Boolean) {
        val alarmMinuteRegisterKey = if(enter) INITIAL_ALARM_MINUTE_KEY else END_ALARM_MINUTE_KEY
        val alarmHourRegisterKey = if(enter) INITIAL_ALARM_HOUR_KEY else END_ALARM_HOUR_KEY
        listOfMemory[alarmMinuteRegisterKey] =  minute.toString()
        listOfMemory[alarmHourRegisterKey] =  hour.toString()
    }

    override fun getHourAlarm(enter: Boolean): Int {
        val alarmHourRegisterKey = if(enter) INITIAL_ALARM_HOUR_KEY else END_ALARM_HOUR_KEY
        return listOfMemory[alarmHourRegisterKey]?.toInt() ?: if (enter) DEFAULT_INITIAL_ALARM_HOUR else DEFAULT_FINAL_ALARM_HOUR
    }

    override fun getMinuteAlarm(enter: Boolean): Int {
        val alarmMinuteRegisterKey = if(enter) INITIAL_ALARM_MINUTE_KEY else END_ALARM_MINUTE_KEY
        return listOfMemory[alarmMinuteRegisterKey]?.toInt() ?: if (enter) DEFAULT_INITIAL_ALARM_MINUTE else DEFAULT_EXIT_ALARM_MINUTE
    }
}