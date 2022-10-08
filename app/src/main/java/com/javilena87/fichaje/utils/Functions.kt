package com.javilena87.fichaje.utils

import java.util.*

fun Calendar.setEndOfDay() {
    set(Calendar.HOUR, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}