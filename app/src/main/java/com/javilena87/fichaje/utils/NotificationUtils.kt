package com.javilena87.fichaje.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.javilena87.fichaje.R
import com.javilena87.fichaje.receiver.CHANNEL_ID
import com.javilena87.fichaje.receiver.FichajeReceiver

const val NOTIFICATION_ENTRANCE_FAILURE = "ENTRANCE_FAILURE"
const val NOTIFICATION_EXIT_FAILURE = "EXIT_FAILURE"
const val NOTIFICATION_ID = "NOTIFICATION_FICHAJE_ID"

fun createNotification(context: Context, enter: Boolean, result: Boolean) {
    val notificationId = System.currentTimeMillis().toInt()
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(context.getString(R.string.notification_title_text))
        .setContentText(
            context.getString(
                R.string.notification_text,
                context.getString(getEnterText(enter)),
                context.getString(getResultText(result))
            )
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    if (!result) {
        val retryIntent =
            Intent(context, FichajeReceiver::class.java).apply {
                action =
                    if (enter) NOTIFICATION_ENTRANCE_FAILURE else NOTIFICATION_EXIT_FAILURE
                putExtra(NOTIFICATION_ID, notificationId)
            }
        val retryPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                retryIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        builder.addAction(
            R.drawable.ic_notification_retry,
            context.getString(R.string.notification_retry_text),
            retryPendingIntent
        )
    }

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}

fun cancelNotification(
    intent: Intent,
    context: Context
) {
    val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(notificationId)
}

private fun getResultText(result: Boolean): Int {
    return if (result) R.string.notification_ok_text else R.string.notification_ko_text
}

private fun getEnterText(enter: Boolean): Int {
    return if (enter) R.string.notification_enter_text else R.string.notification_exit_text
}