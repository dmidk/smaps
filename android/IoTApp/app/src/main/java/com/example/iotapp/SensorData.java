package com.example.iotapp;

public class SensorData {
    public long timestamp;
    public double lat;
    public double lon;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    public double accelerationXStd;
    public double accelerationYStd;
    public double accelerationZStd;
    public double altitudeMean;
    public double altitudeStd;
    public double horizontalAccuracy;
    public double pressureMean;
    public double pressureStd;
    public double speedMean;
    public double speedStd;
    public double verticalAccuracy;

    public SensorData(long timestamp, double lat, double lon, double accelerationX, double accelerationY, double accelerationZ,
                      double accelerationXStd, double accelerationYStd, double accelerationZStd, double altitudeMean, double altitudeStd,
                      double horizontalAccuracy, double pressureMean, double pressureStd, double speedMean, double speedStd, double verticalAccuracy) {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lon = lon;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
        this.accelerationXStd = accelerationXStd;
        this.accelerationYStd = accelerationYStd;
        this.accelerationZStd = accelerationZStd;
        this.altitudeMean = altitudeMean;
        this.altitudeStd = altitudeStd;
        this.horizontalAccuracy = horizontalAccuracy;
        this.pressureMean = pressureMean;
        this.pressureStd = pressureStd;
        this.speedMean = speedMean;
        this.speedStd = speedStd;
        this.verticalAccuracy = verticalAccuracy;
    }

    // Methods to update sensor data values
    public void updateSensorData(long timestamp, double lat, double lon, double accelerationX, double accelerationY, double accelerationZ,
                                 double accelerationXStd, double accelerationYStd, double accelerationZStd, double altitudeMean, double altitudeStd,
                                 double horizontalAccuracy, double pressureMean, double pressureStd, double speedMean, double speedStd, double verticalAccuracy) {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lon = lon;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
        this.accelerationXStd = accelerationXStd;
        this.accelerationYStd = accelerationYStd;
        this.accelerationZStd = accelerationZStd;
        this.altitudeMean = altitudeMean;
        this.altitudeStd = altitudeStd;
        this.horizontalAccuracy = horizontalAccuracy;
        this.pressureMean = pressureMean;
        this.pressureStd = pressureStd;
        this.speedMean = speedMean;
        this.speedStd = speedStd;
        this.verticalAccuracy = verticalAccuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public double getAccelerationY() {
        return accelerationY;
    }

    public double getAccelerationZ() {
        return accelerationZ;
    }

    public double getAccelerationXStd() {
        return accelerationXStd;
    }

    public double getAccelerationYStd() {
        return accelerationYStd;
    }

    public double getAccelerationZStd() {
        return accelerationZStd;
    }

    public double getAltitudeMean() {
        return altitudeMean;
    }

    public double getAltitudeStd() {
        return altitudeStd;
    }

    public double getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public double getPressureMean() {
        return pressureMean;
    }

    public double getPressureStd() {
        return pressureStd;
    }

    public double getSpeedMean() {
        return speedMean;
    }

    public double getSpeedStd() {
        return speedStd;
    }

    public double getVerticalAccuracy() {
        return verticalAccuracy;
    }
}


