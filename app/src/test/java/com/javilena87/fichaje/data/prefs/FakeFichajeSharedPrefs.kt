package com.javilena87.fichaje.data.prefs

import com.javilena87.fichaje.data.FichajeSharedPrefs

class FakeFichajeSharedPrefs constructor(private var listOfMemory: MutableMap<String, String?> = mutableMapOf()) : FichajeSharedPrefs{
    override fun setUserData(userName: String, password: String) {
        setUserName(userName)
        setPassword(password)
    }

    override fun setUserName(userName: String) {
        listOfMemory.put("userName", userName)
    }

    override fun setPassword(password: String) {
        listOfMemory.put("password", password)
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
        listOfMemory.put("alarmState", checked.toString())
    }

    override fun setEntryRegister() {
        listOfMemory.put("entry", "true")
    }

    override fun setExitRegister() {
        listOfMemory.put("exit", "true")
    }

    override fun setAlarmRegister(hour: Int, minute: Int, enter: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getAlarmMinuteKey(enter: Boolean): String {
        TODO("Not yet implemented")
    }

    override fun getAlarmHourKey(enter: Boolean): String {
        TODO("Not yet implemented")
    }

    override fun getHourAlarm(enter: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun getMinuteAlarm(enter: Boolean): Int {
        TODO("Not yet implemented")
    }
}