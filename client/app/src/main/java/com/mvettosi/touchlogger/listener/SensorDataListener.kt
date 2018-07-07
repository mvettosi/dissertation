package com.mvettosi.touchlogger.listener

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.mvettosi.touchlogger.cache.SensorDataCacheService

class SensorDataListener(owner: Activity) : SensorEventListener {
    var ownerActivity: Activity = owner

    companion object {
        const val SENSOR_EVENT_ACCURACY = "SENSOR_EVENT_ACCURACY"
        const val SENSOR_EVENT_SENSOR_TYPE = "SENSOR_EVENT_SENSOR_TYPE"
        const val SENSOR_EVENT_TIMESTAMP = "SENSOR_EVENT_TIMESTAMP"
        const val SENSOR_EVENT_VALUES = "SENSOR_EVENT_VALUES"
    }

    // SensorEventListener overrides
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need for this at the moment
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            var intent = Intent(ownerActivity, SensorDataCacheService::class.java)
            intent.putExtra(SENSOR_EVENT_ACCURACY, event.accuracy)
            intent.putExtra(SENSOR_EVENT_SENSOR_TYPE, event.sensor.stringType)
            intent.putExtra(SENSOR_EVENT_TIMESTAMP, event.timestamp)
            intent.putExtra(SENSOR_EVENT_VALUES, event.values)
            ownerActivity.startService(intent)
        }
    }

    // Public methods
    fun startRecording() {

    }

    fun stopRecording() {

    }
}