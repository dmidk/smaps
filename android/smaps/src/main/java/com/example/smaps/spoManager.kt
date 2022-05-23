package com.example.smaps


import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import mumayank.com.airlocationlibrary.AirLocation
import com.example.smaps.LocationCallback as LocationCallback
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.app.Activity



//interface LocationCallback: AirLocation.Callback {
//    override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum)  {
//        onFailure()
//    }
//    fun onFailure()
//}
//
//class SpoManager(private val activity: AppCompatActivity) : Activity(), SensorEventListener {
//
//    private var mSensorManager: SensorManager? = null
//    private var mPressure: Sensor? = null
//    var lat: Double? = null
//    var lon: Double? = null
//    var alt: Double? = null
//    var ps: Float? = null
//
//    private var airLocation = AirLocation(this, object : AirLocation.Callback {
//
//        override fun onSuccess(locations: ArrayList<Location>) {
//            // do something
//            // the entire track is sent in locations
//        }
//
//        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
//            // do something
//            // the reason for failure is given in locationFailedEnum
//        }
//
//    })
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //airLocation.start() // CALL .start() WHEN YOU ARE READY TO RECEIVE LOCATION UPDATES
//        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        mPressure = mSensorManager!!.getDefaultSensor(Sensor.TYPE_PRESSURE)
//    }
//
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        airLocation.onActivityResult(requestCode, resultCode, data) // ADD THIS LINE INSIDE onActivityResult
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults) // ADD THIS LINE INSIDE onRequestPermissionResult
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event != null) {
//            ps = event.values[0]
//            Log.e("New Pressure", "$ps")
//        }
//        // Do something with this sensor data.
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // Do something here if sensor accuracy changes.
//        TODO("Not yet implemented")
//    }
//
//    override fun onResume() {
//        // Register a listener for the sensor.
//        super.onResume()
//        mSensorManager!!.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL)
//    }
//
//    override fun onPause() {
//        // Be sure to unregister the sensor when the activity pauses.
//        super.onPause()
//        mSensorManager!!.unregisterListener(this)
//    }
//
//    fun startLocation(callback: LocationCallback) {
//        airLocation = AirLocation(activity,
//            callback = callback)
//    }
//
//    fun start(){
//
//        startLocation(object: LocationCallback {
//            override fun onFailure() {
//                Log.e("failed", "Location Fetch failed for some reason")
//            }
//
//
//            override fun onSuccess(location: ArrayList<Location>) {
//                Log.e("New location", "${location[0].latitude}  --- ${location[0].longitude}")
//                lat = location[0].latitude
//                lon = location[0].longitude
//                alt = location[0].altitude
//            }
//        })
//
//
//        var sensorEventListener: SensorEventListener = object : SensorEventListener {
//            override fun onSensorChanged(event: SensorEvent) {
//                val values = event.values
//                val ps = String.format("%.3f hPa", values[0])
//            }
//
//            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
//                Log.d("MY_APP", "$sensor - $accuracy")
//            }
//        }
//
//        Log.e("lat", "${lat}") // Not necesarrily initialised after lat have a value
//
//        Log.e("ps", "${ps}")
//        //Log.e("sensor", "${sensorEventListener}")
//
//    }
//}




interface LocationCallback: AirLocation.Callbacks { //LocationCallback: AirLocation.Callbacks
    fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum)  {
        onFailure()
    }
    fun onFailure() // onFailed was renamed to onFailure
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
            callbacks = callback) //callbacks = callback
    }

    //fun startBarometer(callback: PressureCallback){
    //
    //}


    fun start(){

        startLocation(object: LocationCallback {
            override fun onFailure() { //onFailed
                Log.e("failed", "Location Fetch failed for some reason")
            }

            override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(location: Location) {
                Log.e("New location", "${location.latitude}, ${location.longitude}, ${location.altitude}")
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

        //Log.e("blabla", "${lat}") // Not necesarrily initialised after lat have a value
        //Log.e("ppppp", "${ps}")
        //Log.e("ppppp", "${sensorEventListener}")

    }
}
