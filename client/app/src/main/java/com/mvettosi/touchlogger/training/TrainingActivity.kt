package com.mvettosi.touchlogger.training

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.mvettosi.touchlogger.R
import com.mvettosi.touchlogger.listener.SensorDataListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class TrainingActivity : AppCompatActivity() {
    private lateinit var sensorDataListener: SensorDataListener

    // AppCompactActivity Overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        typePin.isEnabled = false
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        sensorDataListener = SensorDataListener(this)

//        val sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val list = sm.getSensorList(Sensor.TYPE_ALL)
//        for (s in list) {
//            Log.d("SENSORS", "name: " + s.name)
//            Log.d("SENSORS", "type: " + s.type)
//            Log.d("SENSORS", "stringType: " + s.stringType)
//        }
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
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
