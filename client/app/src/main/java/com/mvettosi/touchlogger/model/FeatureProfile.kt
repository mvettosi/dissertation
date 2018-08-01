package com.mvettosi.touchlogger.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

data class FeatureProfile(val sensorId: Int, val nameId: Int) {
    private var source: Sensor? = null
    private var name: String? = null

    constructor(nameId: Int) : this(-1, nameId)

    fun getSensor(context: Context): Sensor? {
        if (sensorId != -1 && source == null) {
            val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            source = manager.getDefaultSensor(sensorId)
        }
        return source
    }

    fun getName(context: Context): String? {
        if (name == null) {
            name = context.getString(nameId)
        }
        return name
    }
}