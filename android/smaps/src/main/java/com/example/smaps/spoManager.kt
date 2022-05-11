package com.example.smaps

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

interface LocationCallback: AirLocation.Callbacks {
    override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum)  {
        onFailed()
    }
    fun onFailed()
}


class SpoManager(private val activity: AppCompatActivity) : Activity(), SensorEventListener {

    private var mSensorManager: SensorManager? = null
    private var mPressure: Sensor? = null

    private var airLocation: AirLocation? = null
    var lat: Double? = null
    var lon: Double? = null
    var alt: Double? = null
    var ps: Float? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get an instance of the sensor service, and use that to get an instance of a particular sensor.
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mPressure = mSensorManager!!.getDefaultSensor(Sensor.TYPE_PRESSURE)
    }

    override fun onSensorChanged(event: SensorEvent) {
        ps = event.values[0]
        Log.e("New Pressure", "${ps}")
        // Do something with this sensor data.
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
        mSensorManager!!.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        mSensorManager!!.unregisterListener(this)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLocation?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        airLocation?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun startLocation(callback: LocationCallback) {
        airLocation = AirLocation(activity,
            shouldWeRequestPermissions = true,
            shouldWeRequestOptimization = true,
            callbacks = callback)
    }

    //fun startBarometer(callback: PressureCallback){
    //
    //}


    fun start(){

        startLocation(object: LocationCallback {
            override fun onFailed() {
                Log.e("failed", "Location Fetch failed for some reason")
            }

            override fun onSuccess(location: Location) {
                Log.e("New location", "${location.latitude}  --- ${location.longitude}")
                lat = location.latitude
                lon = location.longitude
                alt = location.altitude
            }
        })


        var sensorEventListener: SensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val values = event.values
                val ps = String.format("%.3f hPa", values[0])
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.d("MY_APP", "$sensor - $accuracy")
            }
        }

        Log.e("blabla", "${lat}") // Not necesarrily initialised after lat have a value

        Log.e("ppppp", "${ps}")
        Log.e("ppppp", "${sensorEventListener}")

    }
}