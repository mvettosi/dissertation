package com.mvettosi.touchlogger.cache

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.fasterxml.jackson.databind.ObjectMapper
import com.mvettosi.touchlogger.db.CouchdbClient
import org.json.JSONObject
import java.util.*

class DistributionCache(context: Context, size: Int) {
    private var db = CouchdbClient(context)
    private var pinSize = size
    private var mapper = ObjectMapper().writerWithDefaultPrettyPrinter()

    // Thread shared variables
    @Volatile
    private var cache = mutableMapOf<Int, MutableMap<Int, Int>>()
    @Volatile
    private var dbSize = 0

    companion object {
        private const val TAG = "DistributionCache"
        private const val DISTRIBUTION_VIEW = "distribution"
    }

    init {
        db.getView(DISTRIBUTION_VIEW,
                Response.Listener { response -> initCache(response) },
                mutableMapOf(Pair("group", "true")))
        db.getDbInfo(Response.Listener { response ->
            dbSize = response.getInt("doc_count")
            Log.d(TAG, "DB Size: $dbSize")
        })
    }

    //    Public Methods
    fun addPin(pin: String) {
        pin.forEachIndexed { position, c ->
            var digit = c.toString().toInt()
            addOccurrence(position, digit)
        }
        dbSize++
        Log.d(TAG, "New DB Size: $dbSize")
        Log.v(TAG, "Distribution map: ${mapper.writeValueAsString(cache)}")
    }

    fun getPin(): String {
        var result = ""
        if (dbSize % 2 != 0) {
            Log.d(TAG, "Generating random pin")
            result = Random().nextInt(10000).toString().padStart(pinSize, '0')
        } else {
            Log.d(TAG, "Generating best pin")
            for (i in 0 until pinSize) {
                var occurrences = cache.getOrPut(i) { mutableMapOf() }
                var min = occurrences[-1]
                result += min ?: Random().nextInt(10).toString()
            }
        }
        return result
    }

    //    Private Methods
    private fun initCache(response: JSONObject) {
        Log.v(TAG, "Distribution view response body: " + response.toString(4))
        var rows = response.getJSONArray("rows")
        for (i in 0 until rows.length()) {
            var row = rows.getJSONObject(i)
            var keys = row.getJSONArray("key")
            var position = keys.getInt(0)
            var digit = keys.getInt(1)
            var value = row.getInt("value")
            addOccurrence(position, digit, value)
        }
        Log.v(TAG, "Distribution map: ${mapper.writeValueAsString(cache)}")
    }

    private fun addOccurrence(positionToUpdate: Int, digitToUpdate: Int, value: Int = 1) {
        // Insert retrived values into the cache
        var occurrences = cache.getOrPut(positionToUpdate) { mutableMapOf() }
        occurrences[digitToUpdate] = (occurrences[digitToUpdate] ?: 0) + value

        // the digit "-1" is used to keep track of digit with the minimum number of usages for the current position
        var minDigit = 0
        var minOccurrences = occurrences[minDigit] ?: 0
        for (i in 1 until 10) {
            var digitOccurrences = occurrences[i] ?: 0
            if (digitOccurrences < minOccurrences) {
                minDigit = i
                minOccurrences = digitOccurrences
            }
        }
        occurrences[-1] = minDigit
    }
}