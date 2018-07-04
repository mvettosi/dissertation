package com.mvettosi.touchlogger

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Binder
import android.os.IBinder

class LoggerService : Service(), SensorEventListener {
    private val mBinder = LocalBinder()

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LoggerService so clients can call public methods
        fun getService(): LoggerService {
            return this@LoggerService
        }
    }

    // Service overrides
    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    // SensorEventListener implementations
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Nothing to do for now
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Service body
}
