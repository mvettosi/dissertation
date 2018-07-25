package com.mvettosi.touchlogger.training

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    companion object {
        val ACCELLEROMETER = "settingAccelerometer"
        val GYROSCOPE = "settingGyroscope"
        val MAGNETOMETER = "settingMagnetometer"
        val PROXIMITY = "settingProximity"
        val BAROMETER = "settingBarometer"
        val AMBIENT_LIGHT = "settingAmbientLight"
        val ROTATION_VECTOR = "settingRotationVector"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
    }
}
