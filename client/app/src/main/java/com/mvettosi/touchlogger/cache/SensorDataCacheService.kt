package com.mvettosi.touchlogger.cache

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.databind.ObjectMapper
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.ADD_TO_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.CLEAR_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.MESSAGE_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_SENSOR_TYPE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_TIMESTAMP
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.SENSOR_EVENT_VALUES
import com.mvettosi.touchlogger.model.FeatureData
import org.json.JSONObject
import java.util.*


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class SensorDataCacheService : IntentService("SensorDataCacheService") {
    lateinit var queue: RequestQueue

    companion object {
        private val TAG = "SensorDataCacheService"
        private val url = "http://192.168.1.14:5984/raw-data"
        private var cache = mutableMapOf<String, MutableList<FeatureData>>()
        private var mapper = ObjectMapper()
    }

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)
    }

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
            Log.v(TAG, "Got sensor: " + sensorName + ", at: " + timestamp + ", values: " + Arrays.toString(values))
        }
    }

    private fun clearCache() {
        var body = mapper.writeValueAsString(cache)
        Log.v(TAG, "Sending cache: " + prettyJson(body))

        // Request a string response from the provided URL.
        val stringRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                JSONObject(body),
                Response.Listener<JSONObject> { response ->
                    Log.d(TAG, "Sensors document sent")
                    Log.v(TAG, "Response body: ${response.toString(4)}")
                },
                Response.ErrorListener { error -> Log.d(TAG, error.toString()) }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

        //Clear the cache, regardless the outcome of the post request
        cache = mutableMapOf()
    }

    private fun prettyJson(input: String): String {
        return JSONObject(input).toString(4)
    }
}
