package com.example.splashscreenapisample

import android.animation.ObjectAnimator
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import com.example.splashscreenapisample.alarmreceiver.AlarmReceiver

/**
 * @author Nav Singh
 */
class MainActivity : AppCompatActivity() {
    lateinit var content: View
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        content = findViewById(android.R.id.content)
        // splash screen related code 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("MainActivity", "onCreate: I AM RUNNING ON API 12 or higher")
            content.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean =
                    when {
                        mainViewModel.mockDataLoading() -> {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        }
                        else -> false
                    }
            })

            // custom exit on splashScreen
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                // custom animation.
                ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_X,
                    0f,
                    splashScreenView.width.toFloat()
                ).apply {
                    duration = 1000
                    // Call SplashScreenView.remove at the end of your custom animation.
                    doOnEnd {
                        splashScreenView.remove()
                    }
                }.also {
                    // Run your animation.
                    it.start()
                }
            }
        }

        // Exact alarm changes
        findViewById<AppCompatButton>(R.id.button_start_alarm).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // check and set alarms
                val alarmManager: AlarmManager =
                    getSystemService(Context.ALARM_SERVICE) as AlarmManager
                when {
                    alarmManager.canScheduleExactAlarms() -> {
                        // Use to showcase the UX improvements in Android12
                        Toast.makeText(this, getString(R.string.alarm_toast_message), Toast.LENGTH_LONG).show()
                        val alarmIntent =
                            Intent(applicationContext, AlarmReceiver::class.java).let {
                                it.apply { action = getString(R.string.alarm_intent_action) }
                                PendingIntent.getBroadcast(
                                    applicationContext,
                                    0,
                                    it,
                                    FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
                                )
                            }
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            SystemClock.elapsedRealtime() + 60 * 1000,
                            alarmIntent
                        )
                    }
                    else -> {
                        // go to exact alarm settings
                        Intent().apply {
                            action = ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        }.also {
                            startActivity(it)
                        }
                    }
                }
            }
        }
    }
}
