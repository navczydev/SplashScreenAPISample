package com.example.splashscreenapisample.alarmreceiver

import android.app.AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "AlarmReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                Toast.makeText(context, "RECEIVED ALARM PERMISSION", Toast.LENGTH_LONG).show()
            }
            "from alarm" -> {
                Toast.makeText(context, "ALARM FIRED", Toast.LENGTH_LONG).show()
            }
        }
    }
}
