package com.mvettosi.touchlogger.cache

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.mvettosi.touchlogger.db.CouchdbClient
import org.json.JSONObject

class DistributionCache(context: Context) {
    private var db = CouchdbClient(context)
    private var cache = mutableMapOf<Int, MutableMap<Int, Int>>()

    companion object {
        private const val TAG = "DistributionCache"
        private const val DISTRIBUTION_VIEW = "distribution"
    }

    init {
        db.getView(DISTRIBUTION_VIEW,
                Response.Listener { response -> initCache(response) },
                mutableMapOf(Pair("group", "true")))
    }

    private fun initCache(response: JSONObject) {
        Log.v(TAG, "Distribution view response body: " + response.toString(4))
        var rows = response.getJSONArray("rows")
        for (i in 0 until rows.length()) {
            // Fetch json values
            var row = rows.getJSONObject(i)
            var keys = row.getJSONArray("key")
            var position = keys.getInt(0)
            var digit = keys.getInt(1)
            var value = row.getInt("value")

            // Insert retrived values into the cache
            var occurrences = cache.getOrPut(position) { mutableMapOf() }
            occurrences[digit] = value

            // the digit "-1" is used to keep track of digit with the minimum number of usages for the current position
            var currentMin = occurrences[-1]
            var currentMinValue = occurrences[currentMin]
            if (currentMin == null || (currentMinValue != null && value < currentMinValue)) {
                occurrences[-1] = digit
            }
        }
        Log.v(TAG, "Generated distribution map: " + cache.toString())
    }
}