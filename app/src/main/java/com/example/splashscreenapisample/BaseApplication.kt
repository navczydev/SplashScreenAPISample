package com.example.splashscreenapisample

import android.app.Application
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.splashscreenapisample.services.ForegroundService

/**
 * Used to demo the foreground service restrictions
 *
 */
class BaseApplication : Application(), LifecycleEventObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                Thread.sleep(5000)
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        this.startForegroundService(Intent(this, ForegroundService::class.java))
                    }
                    else -> {
                        this.startService(Intent(this, ForegroundService::class.java))
                    }
                }
            }
            else -> {
                // do nothing
            }
        }
    }
}