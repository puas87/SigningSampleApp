package com.javilena87.fichaje.data.db.firebase

import com.google.firebase.database.DatabaseReference
import com.javilena87.fichaje.data.NationalHolidaysDatabaseValueResult
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_DATE_FORMAT
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_DAYTYPE_KEY
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_DAYTYPE_VALUE_HOLYDAY
import com.javilena87.fichaje.utils.FIREBASE_DATABASE_NAME
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume


class FirebaseDataSource(private val reference: DatabaseReference) {

    suspend fun getHolidayFromFirebase(calendar: Calendar): NationalHolidaysDatabaseValueResult =
        suspendCancellableCoroutine { cont ->
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
                        NationalHolidaysDatabaseValueResult.NotValid
                    )
                } else {
                    cont.resume(
                        NationalHolidaysDatabaseValueResult.Success(
                            calendar.timeInMillis
                        )
                    )
                }
            }.addOnFailureListener {
                cont.resume(NationalHolidaysDatabaseValueResult.Error(calendar.timeInMillis))
            }
        }
}