package com.example.splashscreenapisample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * used to demo the notification trampoline restrictions introduced in Android12
 * @author Nav Singh
 */
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // from Android12 this will not work and throw an error:
        // Indirect notification activity start (trampoline) from PACKAGE_NAME, \ this should be
        //avoided for performance reasons.
        context.startActivity(Intent(context, NotificationTrampolineActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })

    }
}