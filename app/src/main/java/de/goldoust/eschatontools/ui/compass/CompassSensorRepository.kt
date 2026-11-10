package de.goldoust.eschatontools.ui.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class SensorOrientationData(
    val azimuth: Float = 0f, // 0..360 degrees
    val pitch: Float = 0f,   // degrees
    val roll: Float = 0f,    // degrees
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
)

class CompassSensorRepository(context: Context) {

    private val sensorManager =
        context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun observeOrientation(): Flow<SensorOrientationData> = callbackFlow {
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // Filtering state
        var smoothSin = 0.0
        var smoothCos = 0.0
        var smoothPitch = 0.0
        var smoothRoll = 0.0
        var isInitialized = false
        val alpha = 0.85 // Low pass filter smoothing factor (0.0 to 1.0)

        val rotationMatrix = FloatArray(9)
        val orientationValues = FloatArray(3)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                var rawAzimuth = 0f
                var rawPitch = 0f
                var rawRoll = 0f

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)

                    rawAzimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                    rawPitch = Math.toDegrees(orientationValues[1].toDouble()).toFloat()
                    rawRoll = Math.toDegrees(orientationValues[2].toDouble()).toFloat()
                } else {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        System.arraycopy(event.values, 0, gravity, 0, 3)
                        hasGravity = true
                    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                        hasGeomagnetic = true
                    }

                    if (hasGravity && hasGeomagnetic) {
                        val R = FloatArray(9)
                        val I = FloatArray(9)
                        if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                            SensorManager.getOrientation(R, orientationValues)
                            rawAzimuth = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                            rawPitch = Math.toDegrees(orientationValues[1].toDouble()).toFloat()
                            rawRoll = Math.toDegrees(orientationValues[2].toDouble()).toFloat()
                        }
                    } else {
                        return
                    }
                }

                // Normalize raw azimuth to 0..360
                if (rawAzimuth < 0) rawAzimuth += 360f

                val currentRad = Math.toRadians(rawAzimuth.toDouble())

                if (!isInitialized) {
                    smoothSin = sin(currentRad)
                    smoothCos = cos(currentRad)
                    smoothPitch = rawPitch.toDouble()
                    smoothRoll = rawRoll.toDouble()
                    isInitialized = true
                } else {
                    // Exponential Low-Pass Filtering on Sin and Cos to prevent wrap-around discontinuity at 0/360
                    smoothSin = alpha * smoothSin + (1.0 - alpha) * sin(currentRad)
                    smoothCos = alpha * smoothCos + (1.0 - alpha) * cos(currentRad)

                    smoothPitch = alpha * smoothPitch + (1.0 - alpha) * rawPitch
                    smoothRoll = alpha * smoothRoll + (1.0 - alpha) * rawRoll
                }

                var filteredAzimuth = Math.toDegrees(atan2(smoothSin, smoothCos)).toFloat()
                if (filteredAzimuth < 0) filteredAzimuth += 360f

                trySend(
                    SensorOrientationData(
                        azimuth = filteredAzimuth,
                        pitch = smoothPitch.toFloat(),
                        roll = smoothRoll.toFloat(),
                        accuracy = event.accuracy
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(
                listener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        } else {
            if (accelSensor != null) {
                sensorManager.registerListener(
                    listener,
                    accelSensor,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
            if (magSensor != null) {
                sensorManager.registerListener(
                    listener,
                    magSensor,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
