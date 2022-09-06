package com.javilena87.fichaje.data.db.firebase

import com.google.firebase.database.DatabaseReference
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_DATE_FORMAT
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_DAYTYPE_KEY
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_DAYTYPE_VALUE_HOLYDAY
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_NAME
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FirebaseDataSource(private val reference: DatabaseReference) {

    suspend fun getHolidayFromFirebase(calendar: Calendar): FirebaseHolidaysDatabaseValueResult =
        suspendCoroutine { cont ->
            val format = SimpleDateFormat(
                FIREBASE_DATABASE_DATE_FORMAT,
                Locale.getDefault()
            ).format(calendar.time)

            val child =
                reference.child(FIREBASE_DATABASE_NAME).child(format).child(
                    FIREBASE_DATABASE_DAYTYPE_KEY
                )
            child.get().addOnSuccessListener {
                if (FIREBASE_DATABASE_DAYTYPE_VALUE_HOLYDAY.equals(it.value)) {
                    cont.resume(
                        FirebaseHolidaysDatabaseValueResult.NotValid
                    )
                } else {
                    cont.resume(
                        FirebaseHolidaysDatabaseValueResult.Success(
                            calendar.timeInMillis
                        )
                    )
                }
            }.addOnFailureListener {
                cont.resume(FirebaseHolidaysDatabaseValueResult.Error(calendar.timeInMillis))
            }
        }
}

sealed class FirebaseHolidaysDatabaseValueResult {
    class Success(val validTime: Long) : FirebaseHolidaysDatabaseValueResult()
    object NotValid :
        FirebaseHolidaysDatabaseValueResult()

    class Error(val currentTime: Long) : FirebaseHolidaysDatabaseValueResult()
}

sealed class FirebaseUsernameDatabaseValueResult {
    object Success :
        FirebaseUsernameDatabaseValueResult()

    object Error :
        FirebaseUsernameDatabaseValueResult()
}