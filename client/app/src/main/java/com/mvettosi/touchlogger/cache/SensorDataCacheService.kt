package com.mvettosi.touchlogger.cache

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.ADD_TO_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheActions.CLEAR_CACHE
import com.mvettosi.touchlogger.cache.SensorDataCacheFields.MESSAGE_TYPE
import com.mvettosi.touchlogger.processor.SensorDataProcessor


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class SensorDataCacheService : IntentService("SensorDataCacheService") {
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.extras?.get(MESSAGE_TYPE.name)) {
            ADD_TO_CACHE -> {
                cache(intent?.extras)
            }
            CLEAR_CACHE -> {
                clearCache()
            }
        }
    }

    private fun cache(extras: Bundle?) {

    }

    private fun clearCache() {
        var processor = SensorDataProcessor()
        processor.cutData()
        processor.cleanData()
        processor.sendData()
    }
}
