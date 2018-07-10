package com.mvettosi.touchlogger.training

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mvettosi.touchlogger.R
import com.mvettosi.touchlogger.listener.SensorDataListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class TrainingActivity : AppCompatActivity() {
    private val sensorDataListener = SensorDataListener(this)

    // AppCompactActivity Overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        typePin.isEnabled = false
        showNewPin()

//        val sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val list = sm.getSensorList(Sensor.TYPE_ALL)
//        for (s in list) {
//            Log.d("SENSORS", "name: " + s.name)
//            Log.d("SENSORS", "type: " + s.type)
//            Log.d("SENSORS", "stringType: " + s.stringType)
//        }
    }

    override fun onResume() {
        super.onResume()
        newPinAlert(null)
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
                return true
            }
        }
        return false
    }

    // Activity Logic
    private fun newPinAlert(previousSucc: Boolean?) {
        var msg = ""
        when (previousSucc) {
            null -> {
                sensorDataListener.discardRecording()
                msg += ""
            }
            true -> {
                sensorDataListener.stopRecording(samplePin.text.toString())
                msg += "Collected!\n"
            }
            false -> {
                sensorDataListener.discardRecording()
                msg += "Discarded!\n"
            }
        }
        msg += "Do you want to insert a new pin?"
        val alertDialog = AlertDialog.Builder(this@TrainingActivity)
                .setMessage(msg)
                .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                    showNewPin()
                    sensorDataListener.startRecording()
                }
                .setNegativeButton("No") { _: DialogInterface, _: Int ->
                    closeApplication()
                }.create()
        alertDialog.show()
    }

    private fun showNewPin() {
        typePin.setText("")
        samplePin.text = Random().nextInt(10000).toString().padStart(4, '0')
    }

    private fun closeApplication() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }

    fun addDigit(view: View) {
        val digit = view.tag.toString()
        val currentText = typePin.text.toString()
        val newText = currentText + digit
        typePin.setText(newText)
        if (newText.length == samplePin.text.length) {
            if (newText == samplePin.text) {
                newPinAlert(true)
            } else {
                newPinAlert(false)
            }
        }
    }
}
