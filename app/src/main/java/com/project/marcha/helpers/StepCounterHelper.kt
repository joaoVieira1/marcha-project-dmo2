package com.project.marcha.helpers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterHelper(
    private val context: Context,
    private val callback: Callback
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var isCounting = false
    private var initialSteps = -1

    fun start() {
        sensor?.let {
            isCounting = true
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        isCounting = false
        initialSteps = -1
    }

    fun hasSensor(): Boolean = sensor != null

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (isCounting && it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = it.values[0].toInt()
                if (initialSteps == -1) {
                    initialSteps = totalSteps
                }
                val currentSteps = totalSteps - initialSteps
                callback.onStepsDetected(currentSteps)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    interface Callback {
        fun onStepsDetected(passos: Int)
    }
}