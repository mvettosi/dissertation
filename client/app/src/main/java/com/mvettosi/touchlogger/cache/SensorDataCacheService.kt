package com.mvettosi.touchlogger.cache

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.ADD_TO_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.CLEAR_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.MESSAGE_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_SENSOR_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_TIMESTAMP
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_VALUES
import com.mvettosi.touchlogger.db.CouchdbClient
import com.mvettosi.touchlogger.model.FeatureData
import java.util.*


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class SensorDataCacheService : IntentService("SensorDataCacheService") {
    private lateinit var db: CouchdbClient

    companion object {
        private const val TAG = "SensorDataCacheService"
        private var cache = mutableMapOf<String, MutableList<FeatureData>>()
    }

    override fun onCreate() {
        super.onCreate()
        db = CouchdbClient(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "${Thread.currentThread().name} onHandleIntent")
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
            // Retrieve Data
            var sensorName = intent.getStringExtra(SENSOR_EVENT_SENSOR_TYPE.name)
            var timestamp = intent.getLongExtra(SENSOR_EVENT_TIMESTAMP.name, 0)
            var values = intent.getFloatArrayExtra(SENSOR_EVENT_VALUES.name)
            var accuracy = intent.getIntExtra(SensorDataCacheFields.SENSOR_EVENT_ACCURACY.name, 0)

            // Add to cache
            var featureList = cache.getOrPut(sensorName) { mutableListOf() }
            featureList.add(FeatureData(timestamp, values, accuracy))
            Log.v(TAG, "Got sensor: " + sensorName + ", at: " + timestamp + ", values: " + Arrays.toString(values))
        }
    }

    private fun clearCache() {
        //Send collected data
        db.sendDocument(cache)
        Log.d(TAG, "Sensors document sent")

        //Clear the cache, regardless the outcome of the post request
        cache = mutableMapOf()
    }
}
