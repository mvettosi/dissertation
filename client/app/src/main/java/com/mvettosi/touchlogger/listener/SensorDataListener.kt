package com.mvettosi.touchlogger.listener

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.ADD_TO_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.CLEAR_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.MESSAGE_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_ACCURACY
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_SENSOR_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_TIMESTAMP
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_VALUES
import com.mvettosi.touchlogger.cache.SensorDataCacheService
import com.mvettosi.touchlogger.training.SettingsActivity.Companion.ACCELLEROMETER
import com.mvettosi.touchlogger.training.SettingsActivity.Companion.AMBIENT_LIGHT
import com.mvettosi.touchlogger.training.SettingsActivity.Companion.BAROMETER
import com.mvettosi.touchlogger.training.SettingsActivity.Companion.GYROSCOPE
import com.mvettosi.touchlogger.training.SettingsActivity.Companion.MAGNETOMETER
import com.mvettosi.touchlogger.training.SettingsActivity.Companion.PROXIMITY
import com.mvettosi.touchlogger.training.SettingsActivity.Companion.ROTATION_VECTOR

class SensorDataListener(owner: Activity) : SensorEventListener {
    private var ownerActivity: Activity = owner
    private var settings = PreferenceManager.getDefaultSharedPreferences(ownerActivity) as SharedPreferences
    private var sensorManager = ownerActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var isRecording = false

    // Sensors
    private var accellerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private var magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private var proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    private var barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    private var light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private var rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

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
        Log.d("SensorDataListener", "Starting to record sensors")
        var delay = SensorManager.SENSOR_DELAY_GAME
        if (settings.getBoolean(ACCELLEROMETER, true)) {
            sensorManager.registerListener(this, accellerometer, delay)
        }
        if (settings.getBoolean(GYROSCOPE, true)) {
            sensorManager.registerListener(this, gyroscope, delay)
        }
        if (settings.getBoolean(MAGNETOMETER, true)) {
            sensorManager.registerListener(this, magnetometer, delay)
        }
        if (settings.getBoolean(PROXIMITY, true)) {
            sensorManager.registerListener(this, proximity, delay)
        }
        if (settings.getBoolean(BAROMETER, true)) {
            sensorManager.registerListener(this, barometer, delay)
        }
        if (settings.getBoolean(AMBIENT_LIGHT, true)) {
            sensorManager.registerListener(this, light, delay)
        }
        if (settings.getBoolean(ROTATION_VECTOR, true)) {
            sensorManager.registerListener(this, rotationVector, delay)
        }
        isRecording = true
    }

    fun stopRecording(newPin: String?) {
        Log.d("SensorDataListener", "Stopping to record sensors")
        sensorManager.unregisterListener(this)
        var intent = Intent(ownerActivity, SensorDataCacheService::class.java)
        intent.putExtra(MESSAGE_TYPE.name, CLEAR_CACHE)
        ownerActivity.startService(intent)
        isRecording = false
    }

    fun discardRecording() {
        Log.d("SensorDataListener", "Discarded recordings")
        sensorManager.unregisterListener(this)
        isRecording = false
    }
}