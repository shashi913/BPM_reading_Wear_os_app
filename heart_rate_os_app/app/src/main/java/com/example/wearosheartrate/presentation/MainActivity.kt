package com.example.wearosheartrate.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
//import com.google.android.gms.wearable.DataItem.fromDataMap
import com.google.android.gms.wearable.Wearable
import com.example.wearosheartrate.R

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val heartRatePath = "/heart_rate"
    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register listener for heart rate sensor
        val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            val heartRate = event.values[0]
            sendDataToPhone(heartRate)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes
    }

    private fun sendDataToPhone(heartRate: Float) {
        val dataMap = PutDataMapRequest.create(heartRatePath).dataMap
        dataMap.putFloat("heart_rate", heartRate)

        val request = PutDataRequest.create(heartRatePath)
        request.data = dataMap.toByteArray()

        Wearable.getDataClient(this).putDataItem(request)
        //Wearable.getDataClient(this).putDataItem(DataItem.fromDataMap(dataMap))
            .addOnSuccessListener {
                // Data sent successfully
            }
            .addOnFailureListener {exception ->
                // Handle data transfer failure
                showToast("Data transfer failed: ${exception.message}")
            }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}
