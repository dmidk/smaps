package com.example.iotapp;

import static java.lang.Double.NaN;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import android.os.Build;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.List;
import java.util.ArrayList;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private LocationManager locationManager;
    private double lat = 0.0; // Initialize with default value
    private double lon = 0.0; // Initialize with default value
    private double accelerationX = 0.0; // Initialize with default value
    private double accelerationY = 0.0; // Initialize with default value
    private double accelerationZ = 0.0; // Initialize with default value

    private double altitudeMean = 0.0; // Initialize with default value
    private double altitudeStd = 0.0; // Initialize with default value
    private double horizontalAccuracy = 0.0; // Initialize with default value
    private double verticalAccuracy = 0.0; // Initialize with default value
    private double speedMean = 0.0; // Initialize with default value
    private double pressureMean = 0.0; // Initialize with default value
    double accelerationXStd = 0.0; // Initialize with default value
    double accelerationYStd = 0.0; // Initialize with default value
    double accelerationZStd = 0.0; // Initialize with default value

    double pressureStd = 0.0; // Initialize with default value

    double speedStd = 0.0; // Initialize with default value

    private List<Double> accelerationXValues = new ArrayList<>();
    private List<Double> accelerationYValues = new ArrayList<>();
    private List<Double> accelerationZValues = new ArrayList<>();
    private List<Double> altitudeValues = new ArrayList<>();
    private List<Double> pressureValues = new ArrayList<>();
    private List<Double> speedValues = new ArrayList<>();

    public SensorData sensorData = new SensorData(System.currentTimeMillis(), lat, lon, accelerationX, accelerationY, accelerationZ,
            accelerationXStd, accelerationYStd, accelerationZStd, altitudeMean, altitudeStd,
            horizontalAccuracy, pressureMean, pressureStd, speedMean, speedStd, verticalAccuracy);
    private List<SensorData> sensorDataList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Permission already granted, start requesting location updates
            startLocationUpdates();
        }

        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : allSensors) {
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //Schedule the task to run every 7 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                calculateSensorData();
                handler.postDelayed(this, 7000);
            }
        }, 7000);
    }


    public void writeNewSensorData(String sensorId, long timestamp, double lat, double lon, double accelerationX, double accelerationY, double accelerationZ,
                                   double accelerationXStd, double accelerationYStd, double accelerationZStd, double altitudeMean, double altitudeStd,
                                   double horizontalAccuracy, double pressureMean, double pressureStd, double speedMean, double speedStd, double verticalAccuracy) {
        SensorData sensor = new SensorData(timestamp, lat, lon, accelerationX, accelerationY, accelerationZ,
        accelerationXStd, accelerationYStd, accelerationZStd, altitudeMean, altitudeStd,
        horizontalAccuracy, pressureMean, pressureStd, speedMean, speedStd, verticalAccuracy);
        mDatabase.child("sensors").child(sensorId).setValue(sensor);
    }

    private void calculateSensorData() {
        // Calculate mean and standard deviation for accelerationX
        double sumX = 0.0;
        for (double value : accelerationXValues) {
            sumX += value;
        }
        accelerationX = sumX / accelerationXValues.size();

        double sumSqX = 0.0;
        for (double value : accelerationXValues) {
            sumSqX += Math.pow(value - accelerationX, 2);
        }
        accelerationXStd = Math.sqrt(sumSqX / accelerationXValues.size());

        // Calculate mean and standard deviation for accelerationY
        double sumY = 0.0;
        for (double value : accelerationYValues) {
            sumY += value;
        }
        accelerationY= sumY / accelerationYValues.size();

        double sumSqY = 0.0;
        for (double value : accelerationYValues) {
            sumSqY += Math.pow(value - accelerationY, 2);
        }
        accelerationYStd = Math.sqrt(sumSqY / accelerationYValues.size());

        // Calculate mean and standard deviation for accelerationZ
        double sumZ = 0.0;
        for (double value : accelerationZValues) {
            sumZ += value;
        }
        accelerationZ= sumZ / accelerationZValues.size();
        double sumSqZ = 0.0;
        for (double value : accelerationZValues) {
            sumSqZ += Math.pow(value - accelerationZ, 2);
        }
        accelerationZStd = Math.sqrt(sumSqZ / accelerationZValues.size());

        // Calculate mean and standard deviation for pressure
        if (pressureValues.isEmpty()) {
            pressureMean = 0;
            pressureStd = 0;
        } else {
            double sumPr = 0.0;
            for (double value : pressureValues) {
                sumPr += value;
            }
            pressureMean = sumPr / pressureValues.size();
            double sumSqPr = 0.0;
            for (double value : pressureValues) {
                sumSqPr += Math.pow(value - pressureMean, 2);
            }
            pressureStd = Math.sqrt(sumSqPr / pressureValues.size());
        }
        // Calculate mean and standard deviation for speed
        double sumSp = 0.0;
        for (double value : speedValues) {
            sumSp += value;
        }
        speedMean= sumSp / speedValues.size();
        double sumSqSp = 0.0;
        for (double value : speedValues) {
            sumSqSp += Math.pow(value - speedMean, 2);
        }
        speedStd = Math.sqrt(sumSqSp / speedValues.size());

        // Calculate mean and standard deviation for altitude
        if (altitudeValues.isEmpty()) {
            altitudeMean = 0;
            altitudeStd = 0;
        } else {
            double sumAlt = 0.0;
            for (double value : altitudeValues) {
                sumAlt += value;
            }
            altitudeMean = sumAlt / altitudeValues.size();
            double sumSqAlt = 0.0;
            for (double value : altitudeValues) {
                sumSqAlt += Math.pow(value - altitudeMean, 2);
            }
            altitudeStd = Math.sqrt(sumSqAlt / altitudeValues.size());
        }
        // Clear the lists to start fresh for the next 7 seconds
        accelerationXValues.clear();
        accelerationYValues.clear();
        accelerationZValues.clear();
        pressureValues.clear();
        speedValues.clear();
        altitudeValues.clear();

        sensorData.updateSensorData(System.currentTimeMillis(), lat, lon, accelerationX, accelerationY, accelerationZ,
                accelerationXStd, accelerationYStd, accelerationZStd, altitudeMean, altitudeStd,
                horizontalAccuracy, pressureMean, pressureStd, speedMean, speedStd, verticalAccuracy);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        writeNewSensorData(String.valueOf(sensorData.timestamp), sensorData.timestamp, lat, lon, accelerationX, accelerationY, accelerationZ,
                accelerationXStd, accelerationYStd, accelerationZStd, altitudeMean, altitudeStd,
                horizontalAccuracy, pressureMean, pressureStd, speedMean, speedStd, verticalAccuracy);
        // Add new SensorData to the list
        sensorDataList.add(sensorData);

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            horizontalAccuracy = location.getAccuracy();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                verticalAccuracy = location.getVerticalAccuracyMeters();
            }
            speedValues.add((double) location.getSpeed());
            altitudeValues.add((double) location.getAltitude());
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;

            // Check if event has values and handle based on sensor type
            if (event.values.length > 0) {
                long timestamp = System.currentTimeMillis(); // Current timestamp

                // Populate values based on sensor type
                switch (sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        if (event.values.length >= 3) {
                            accelerationXValues.add((double) event.values[0]);
                            accelerationYValues.add((double) event.values[1]);
                            accelerationZValues.add((double) event.values[2]);
                        }
                        break;
                    case Sensor.TYPE_PRESSURE:
                        if (!Double.isNaN(event.values[0])) {
                            pressureValues.add((double) event.values[0]);
                        }
                        break;
                    // Add cases for other sensor types as needed
                }

                // TODO: compute mean and std

            } else {
                // No values in the event
                System.out.println("No values in sensor event.");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            System.out.println("Accuracy is: " + accuracy);
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start location updates
            startLocationUpdates();
        }
    }

}