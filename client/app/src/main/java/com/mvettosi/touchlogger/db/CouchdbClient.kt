package com.mvettosi.touchlogger.db

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject

class CouchdbClient(context: Context) {
    private var queue = Volley.newRequestQueue(context)

    companion object {
        private const val TAG = "CouchdbClient"
        private const val protocol = "http"
        private const val host = "Matteos-iMac.connect"
        private const val port = "5984"
        private const val db = "raw-data"
        private const val design = "views"

        private var mapper = ObjectMapper()
    }

    fun sendDocument(doc: Any) {
        var body = mapper.writeValueAsString(doc)
        Log.v(TAG, "Sending cache: " + prettyJson(body))

        // Request a string response from the provided URL.
        val request = JsonObjectRequest(
                Request.Method.POST,
                getDbUrl(),
                JSONObject(body),
                Response.Listener<JSONObject> { response ->
                    Log.v(TAG, "Response body: ${response.toString(4)}")
                },
                Response.ErrorListener { error -> Log.d(TAG, error.toString()) }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    fun getDesign(success: Response.Listener<JSONObject>, error: Response.ErrorListener = Response.ErrorListener { error -> Log.d(TAG, error.toString()) }) {
        val request = JsonObjectRequest(
                "${getDbUrl()}/_design/views",
                null,
                success,
                error
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    fun sendDesign(views: JSONObject, success: Response.Listener<JSONObject>, error: Response.ErrorListener = Response.ErrorListener { error -> Log.d(TAG, error.toString()) }) {
        val request = JsonObjectRequest(
                Request.Method.PUT,
                "${getDbUrl()}/_design/views",
                views,
                success,
                error
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    fun getView(name: String, success: Response.Listener<JSONObject>, params: MutableMap<String, String>) {
        val request = JsonObjectRequest(
                Request.Method.GET,
                getViewUrl(name, params),
                JSONObject(params),
                success,
                Response.ErrorListener { error -> Log.d(TAG, error.toString()) }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    fun getDbInfo(success: Response.Listener<JSONObject>) {
        val request = JsonObjectRequest(
                Request.Method.GET,
                getDbUrl(),
                null,
                success,
                Response.ErrorListener { error -> Log.d(TAG, error.toString()) }
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

//    fun getView(name: String, vararg params: Pair<String, String>): JSONObject {
//        var future = RequestFuture.newFuture<JSONObject>()
//        val request = object : JsonObjectRequest(
//                getViewUrl(name),
//                null,
//                future,
//                future
//        ) {
//            override fun getParams(): MutableMap<String, String> {
//                var result = mutableMapOf<String, String>()
//                if (params.isEmpty()) {
//                    result = super.getParams()
//                } else {
//                    for (pair in params) {
//                        result[pair.first] = pair.second
//                    }
//                }
//                return result
//            }
//        }
//        queue.add(request)
//        return future.get()
//    }

    private fun getDbUrl(): String {
        return "$protocol://$host:$port/$db"
    }

    private fun getViewUrl(view: String, params: MutableMap<String, String>): String {
        var queryBuilder = StringBuilder()
        for ((key, value) in params) {
            if (!queryBuilder.isEmpty()) {
                queryBuilder.append("&")
            }
            queryBuilder.append(key).append("=").append(value)
        }
        return "$protocol://$host:$port/$db/_design/$design/_view/$view?$queryBuilder"
    }

    private fun prettyJson(input: String): String {
        return JSONObject(input).toString(4)
    }
}