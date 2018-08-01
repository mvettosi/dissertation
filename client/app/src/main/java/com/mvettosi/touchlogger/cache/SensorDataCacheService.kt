package com.mvettosi.touchlogger.cache

import android.app.IntentService
import android.content.Intent
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.ADD_TO_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.CLEAR_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.MESSAGE_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_SENSOR_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_TIMESTAMP
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_VALUES
import com.mvettosi.touchlogger.model.FeatureData
import com.mvettosi.touchlogger.processor.SensorDataProcessor
import java.util.*


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class SensorDataCacheService : IntentService("SensorDataCacheService") {
    private var cache = mutableMapOf<String, MutableList<FeatureData>>()

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.extras?.get(MESSAGE_TYPE.name)) {
            ADD_TO_CACHE -> {
                cache(intent)
            }
            CLEAR_CACHE -> {
                clearCache()
            }
        }
    }

    private fun cache(intent: Intent?) {
        if (intent != null) {
            var sensorName = intent.getStringExtra(SENSOR_EVENT_SENSOR_TYPE.name)
            var timestamp = intent.getLongExtra(SENSOR_EVENT_TIMESTAMP.name, 0)
            var values = intent.getFloatArrayExtra(SENSOR_EVENT_VALUES.name)
            var accuracy = intent.getIntExtra(SensorDataCacheFields.SENSOR_EVENT_ACCURACY.name, 0)
            if (!cache.containsKey(sensorName)) {
                cache[sensorName] = mutableListOf()
            }
            cache[sensorName]?.add(FeatureData(timestamp, values, accuracy))
//            Log.d("CACHE", "Got sensor: " + sensorName + ", at: " + timestamp + ", values: " + Arrays.toString(values))
        }
    }

    private fun clearCache() {
        var processor = SensorDataProcessor()
        processor.cutData()
        processor.cleanData()
        processor.sendData(cache)
        cache = HashMap()
    }
}
