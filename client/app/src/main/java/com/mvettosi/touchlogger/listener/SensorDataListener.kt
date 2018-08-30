package com.mvettosi.touchlogger.listener

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
import com.mvettosi.touchlogger.training.TrainingActivity

class SensorDataListener(owner: TrainingActivity) : SensorEventListener {
    private var context: TrainingActivity = owner
    private var settings = PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
    private var sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var isRecording = false

    companion object {
        private val TAG = "SensorDataListener"
    }

    // SensorEventListener overrides
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need for this at the moment
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            var intent = Intent(context, SensorDataCacheService::class.java)
            intent.putExtra(SENSOR_EVENT_ACCURACY.name, event.accuracy)
            intent.putExtra(SENSOR_EVENT_SENSOR_TYPE.name, event.sensor.stringType)
            intent.putExtra(SENSOR_EVENT_TIMESTAMP.name, event.timestamp)
            intent.putExtra(SENSOR_EVENT_VALUES.name, event.values)
            intent.putExtra(MESSAGE_TYPE.name, ADD_TO_CACHE)
            context.startService(intent)
        }
    }

    // Public methods
    fun startRecording() {
        Log.d(TAG, "Starting to record sensors")
        var delay = SensorManager.SENSOR_DELAY_GAME

        for (sensor in context.features) {
            if (settings.getBoolean(sensor.getName(context), false)) {
                sensorManager.registerListener(this, sensor.getSensor(context), delay)
            }
        }

        isRecording = true
    }

    fun stopRecording() {
        Log.d(TAG, "Stopping to record sensors")
        sensorManager.unregisterListener(this)
        var intent = Intent(context, SensorDataCacheService::class.java)
        intent.putExtra(MESSAGE_TYPE.name, CLEAR_CACHE)
        context.startService(intent)
        isRecording = false
    }

    fun discardRecording() {
        Log.d(TAG, "Discarded recordings")
        sensorManager.unregisterListener(this)
        isRecording = false
    }

    fun addFeatureValue(feature: String, timestamp: Long, values: FloatArray?) {
        var intent = Intent(context, SensorDataCacheService::class.java)
        intent.putExtra(SENSOR_EVENT_SENSOR_TYPE.name, feature)
        intent.putExtra(SENSOR_EVENT_TIMESTAMP.name, timestamp)
        if (values != null) {
            intent.putExtra(SENSOR_EVENT_VALUES.name, values)
        }
        intent.putExtra(MESSAGE_TYPE.name, ADD_TO_CACHE)
        context.startService(intent)
    }
}