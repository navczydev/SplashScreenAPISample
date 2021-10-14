package com.example.splashscreenapisample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * @author Nav Singh
 */
private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

fun sendNotification(context: Context) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val broadcastIntent = Intent(context.applicationContext, NotificationReceiver::class.java)
    val actionIntent = PendingIntent.getBroadcast(
        context.applicationContext,
        0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    // TODO fix by adding pending intent
    val notificationTrampolineActivityIntent =
        Intent(context.applicationContext, NotificationTrampolineActivity::class.java)
    // Create the TaskStackBuilder
    val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
        // Add the intent, which inflates the back stack
        addNextIntentWithParentStack(notificationTrampolineActivityIntent)
        // Get the PendingIntent containing the entire back stack
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Android12")
        .setContentText("Notification trampoline restrictions")
//        .addAction(R.mipmap.ic_launcher, "Open activity from receiver", actionIntent)
        .addAction(R.mipmap.ic_launcher, "Open activity from receiver", resultPendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(getUniqueId(), notification)
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())