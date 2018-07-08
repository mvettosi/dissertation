package com.mvettosi.touchlogger.listener

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.ADD_TO_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.CLEAR_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.*
import com.mvettosi.touchlogger.cache.SensorDataCacheService

class SensorDataListener(owner: Activity) : SensorEventListener {
    var ownerActivity: Activity = owner

    // SensorEventListener overrides
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need for this at the moment
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            var intent = Intent(ownerActivity, SensorDataCacheService::class.java)
            intent.putExtra(SENSOR_EVENT_ACCURACY.name, event.accuracy)
            intent.putExtra(SENSOR_EVENT_SENSOR_TYPE.name, event.sensor.stringType)
            intent.putExtra(SENSOR_EVENT_TIMESTAMP.name, event.timestamp)
            intent.putExtra(SENSOR_EVENT_VALUES.name, event.values)
            intent.putExtra(MESSAGE_TYPE.name, ADD_TO_CACHE)
            ownerActivity.startService(intent)
        }
    }

    // Public methods
    fun startRecording() {
        //Fetch settings, register as listener for all the set sensors
    }

    fun stopRecording() {
        //Unresgister from all sensors, then ask cache to process and send data

        var intent = Intent(ownerActivity, SensorDataCacheService::class.java)
        intent.putExtra(MESSAGE_TYPE.name, CLEAR_CACHE)
        ownerActivity.startService(intent)
    }
}