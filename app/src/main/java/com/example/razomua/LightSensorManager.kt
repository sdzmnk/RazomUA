package com.example.razomua

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class LightSensorManager(
    private val context: Context,
    private val onLightLevelChanged: (Boolean) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val DARK_THRESHOLD = 20f

    fun start() {
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val lux = event?.values?.get(0) ?: return

        val isDark = lux < DARK_THRESHOLD
        onLightLevelChanged(isDark)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
