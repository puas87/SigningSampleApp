package com.javilena87.fichaje.presentation.calendar.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.javilena87.fichaje.R
import com.javilena87.fichaje.data.db.HolidayReg
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val DATE_FORMAT: String = "dd/MM/yyyy"

@BindingAdapter("holidayFormatString")
fun TextView.setHolidayFormatString(day: Long?) {
    day?.let {
        text = convertNumericDateToString(day)
    }
}

@BindingAdapter("totalDaysString")
fun TextView.setTotalDaysString(item: HolidayReg?) {
    item?.let {
        text = getTotalDays(item, context.getString(R.string.days_text))
    }
}

fun getTotalDays(item: HolidayReg, complementStr: String): String {
    val difference = item.dayOut - item.dayIn
    var days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
    if (days == 0L) days++
    return "$days $complementStr"
}

fun convertNumericDateToString(day: Long): String {
    val dateFormat =
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val dayCalendar = Calendar.getInstance()
    dayCalendar.timeInMillis = day
    return dateFormat.format(dayCalendar.time)
}