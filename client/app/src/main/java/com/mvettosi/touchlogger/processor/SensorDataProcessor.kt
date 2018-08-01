package com.mvettosi.touchlogger.processor

import android.util.Log
import com.mvettosi.touchlogger.model.FeatureData

class SensorDataProcessor {
    fun cutData() {

    }

    fun cleanData() {

    }

    fun sendData(cache: MutableMap<String, MutableList<FeatureData>>) {
        Log.d("DATA_SENT", cache.toString())
    }
}