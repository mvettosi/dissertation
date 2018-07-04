package com.mvettosi.touchlogger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mvettosi.touchlogger.LoggerService.LocalBinder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class LockscreenActivity : AppCompatActivity() {
    lateinit var pinStarted: Date
    lateinit var logger: LoggerService
    var mBound = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalBinder
            logger = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    //Activity Overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        typePin.isEnabled = false
        newPin()
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

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LoggerService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(mConnection)
        mBound = false
    }

    //Activity Body
    private fun newPin() {
        typePin.setText("")
        samplePin.text = Random().nextInt(10000).toString().padStart(4, '0')
    }

    fun addDigit(view: View) {
        val digit = view.tag.toString()
        val currentText = typePin.text.toString()
        if (currentText.isEmpty()) {
            startPinRecording();
        }
        val newText = currentText + digit
        if (newText == samplePin.text) {
            completePinRecording(newText);
        }
        if (newText.length == 4) {
            newPin()
        } else {
            typePin.setText(newText)
        }
    }

    private fun startPinRecording() {
//        pinStarted = Date(System.currentTimeMillis() - 1000)
    }

    private fun completePinRecording(newPin: String) {
        alert("Right!")
    }

    private fun alert(s: String) {
        val alertDialog = AlertDialog.Builder(this@LockscreenActivity).create()
        alertDialog.setMessage(s)
        alertDialog.show()
    }
}
