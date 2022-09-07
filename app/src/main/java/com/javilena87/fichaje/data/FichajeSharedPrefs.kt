package com.javilena87.fichaje.data

interface FichajeSharedPrefs {
    fun setUserData(userName: String, password: String)
    fun setUserName(userName: String)
    fun setPassword(password: String)
    fun getUsername(fallback: String = ""): String
    fun getPassword(fallback: String = ""): String
    fun getAlarmState(): Boolean
    fun setAlarmState(checked: Boolean)
    fun setEntryRegister()
    fun setExitRegister()
    fun setAlarmRegister(hour: Int, minute: Int, enter: Boolean)
    fun getAlarmMinuteKey(enter: Boolean): String
    fun getAlarmHourKey(enter: Boolean): String
    fun getHourAlarm(enter: Boolean): Int
    fun getMinuteAlarm(enter: Boolean): Int
}