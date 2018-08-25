package com.mvettosi.touchlogger.training

import android.content.Intent
import android.hardware.Sensor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.mvettosi.touchlogger.R
import com.mvettosi.touchlogger.cache.DistributionCache
import com.mvettosi.touchlogger.listener.SensorDataListener
import com.mvettosi.touchlogger.model.FeatureProfile
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class TrainingActivity : AppCompatActivity() {
    private lateinit var sensorDataListener: SensorDataListener
    private lateinit var pinGenerator: DistributionCache

    var features = listOf(
            FeatureProfile(Sensor.TYPE_ACCELEROMETER, R.string.accelerometer),
            FeatureProfile(Sensor.TYPE_GYROSCOPE, R.string.gyroscope),
            FeatureProfile(Sensor.TYPE_MAGNETIC_FIELD, R.string.magnetometer),
            FeatureProfile(Sensor.TYPE_PROXIMITY, R.string.proximity),
            FeatureProfile(Sensor.TYPE_PRESSURE, R.string.barometer),
            FeatureProfile(Sensor.TYPE_LIGHT, R.string.ambient_light),
            FeatureProfile(Sensor.TYPE_ROTATION_VECTOR, R.string.rotation_vector),
            FeatureProfile(R.string.digit)
    )

    // AppCompactActivity Overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        typePin.isEnabled = false
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        sensorDataListener = SensorDataListener(this)
        pinGenerator = DistributionCache(this)
    }

    override fun onPause() {
        super.onPause()
        if (sensorDataListener.isRecording) {
            sensorDataListener.discardRecording()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_newpin -> {
                if (sensorDataListener.isRecording) {
                    sensorDataListener.discardRecording()
                }
                showNewPin()
                sensorDataListener.startRecording()
                true
            }
        }
        return false
    }

    private fun showNewPin() {
        typePin.setText("")
        samplePin.text = Random().nextInt(10000).toString().padStart(4, '0')
    }

    fun addDigit(view: View) {
        if (sensorDataListener.isRecording) {
            val digit = view.tag.toString()
            val now = System.currentTimeMillis()
            sensorDataListener.addFeatureValue(this.getString(R.string.digit), now, floatArrayOf(digit.toFloat()))
            val currentText = typePin.text.toString()
            val newText = currentText + digit
            typePin.setText(newText)
            if (newText.length == samplePin.text.length) {
                if (newText == samplePin.text) {
                    sensorDataListener.stopRecording(samplePin.text.toString())
                    toast("Collected!")
                } else {
                    sensorDataListener.discardRecording()
                    toast("Discarded!")
                }
            }
        }
    }

    fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
