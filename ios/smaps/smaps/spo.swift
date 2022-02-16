//
//  spo.swift
//  smaps
//
//  Created by Kasper Hintz on 05/09/2019.
//  Copyright © 2019 Kasper Hintz. All rights reserved.
//
//  Main Swifty
//

import Foundation
import CoreMotion
import CoreLocation

extension Date {
    func toMillis() -> Int64! {
        return Int64(self.timeIntervalSince1970 * 1000)
    }
}

public struct smapsObsObj{
    public let timestamp:Int
    public let lat:Double
    public let lon:Double
    public let accelerationX:Double
    public let accelerationY:Double
    public let accelerationZ:Double
    public let accelerationXStd:Double
    public let accelerationYStd:Double
    public let accelerationZStd:Double
    public let altidudeMean:Double
    public let altidudeStd:Double
    public let horisontalAccuracy:Double
    public let pressureMean:Double
    public let pressureStd:Double
    public let speedMean:Double
    public let speedStd:Double
    public let verticalAccuracy:Double
}

public struct smapsMesId{
    public let mesid:Int
}


public class SpoLocation : NSObject, CLLocationManagerDelegate {

    var _appKey = ""

    // Location
    var locationManager = CLLocationManager()
    var lat: String
    var lon: String
    var altitude: Double
    var speed: Double
    public var callbackLocation: ((CLLocation) -> Void)? = nil

    public var obscompleted : ((smapsObsObj) -> (Void))? = nil

    public var mesid : ((smapsMesId) -> (Void))? = nil

    // Barometer
    let altimeter = CMAltimeter()

    // Acceleration
    let motionManager = CMMotionManager()

    // Variables to upload / updates at each change
    var _pressure : Double?
    var _lat : Double?
    var _lon : Double?

    var _ax : Double?
    var _ay : Double?
    var _az : Double?

    var responses = ["_p": 0.0,
                     "_lat": 0.0,
                     "_lon": 0.0,
                     "_ax": 0.0,
                     "_ay": 0.0,
                     "_az": 0.0,
                     "_altitude": 0.0,
                     "_speed": 0.0,
                     "_horizontalAccuracy": 0.0,
                     "_verticalAccuracy": 0.0
    ]


    private func start_location_updates() -> Void {
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
    }


    private func startTracking(callbackLocation: @escaping (CLLocation) -> Void) {
        self.callbackLocation = callbackLocation
        locationManager.startUpdatingLocation()
        locationManager.startUpdatingHeading()

        if UIDevice.current.orientation == .portraitUpsideDown {
            locationManager.headingOrientation = .portraitUpsideDown
        }
    }


    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) -> Void {

        let locValue = manager.location!
        lat = locValue.coordinate.latitude.description
        lon = locValue.coordinate.longitude.description
        altitude = locValue.altitude
        speed = locValue.speed
        self.callbackLocation?(locValue)
    }


    private func start_barometer_updates() -> Void  {
        if CMAltimeter.isRelativeAltitudeAvailable() {
            altimeter.startRelativeAltitudeUpdates(to: OperationQueue.main) { (data, error) in
                let kpa = (data?.pressure.doubleValue ?? 0) * 10.0
                self._pressure = kpa
                self.responses["_p"] = kpa
            }
        }
    }


    private func start_accelerometer_updates() -> Void {
        motionManager.startAccelerometerUpdates(to: .main) {accelerometerData, error in
            self.motionManager.accelerometerUpdateInterval = 0.1
            
            if let ax = self.motionManager.accelerometerData?.acceleration.x {
                self._ax = ax*9.8
                self.responses["_ax"] = self._ax
            }
            if let ay = self.motionManager.accelerometerData?.acceleration.y {
                self._ay = ay*9.8
                self.responses["_ay"] = self._ay
            }
            if let az = self.motionManager.accelerometerData?.acceleration.z {
                self._az = az*9.8
                self.responses["_az"] = self._az
            }
        }
    }


    public func smaps_start() {

        start_location_updates()
        startTracking() {
            self.responses["_lat"] = $0.coordinate.latitude
            self.responses["_lon"] = $0.coordinate.longitude
            self.responses["_altitude"] = $0.altitude
            self.responses["_speed"] = $0.speed
            self.responses["_horizontalAccuracy"] = $0.horizontalAccuracy
            self.responses["_verticalAccuracy"] = $0.verticalAccuracy
        }
        start_barometer_updates()
        start_accelerometer_updates()

        let maskLocation = mask_location()

        // LOGIC OF SMAPS
        var index: Double = 1
        var indexSpinup: Int = 0
        let spinUpThreshold: Int = 5 // Dont start a measurement before spinUp has passed
        let uploadThreshold: Double = 7 // Average over N measurements

        var pressureAvg: Double = 0.0
        var pressureStd: Double = 0.0
        var altitudeAvg: Double = 0.0
        var altitudeStd: Double = 0.0
        var height: Double = 0.0
        var verticalaccuracy: Double = 0.0
        var horisontalaccuracy: Double = 0.0
        var speedAvg: Double = 0.0
        var speedStd: Double = 0.0

        // ACCELERATION //
        var axAvg: Double = 0.0
        var ayAvg: Double = 0.0
        var azAvg: Double = 0.0
        var axStd: Double = 0.0
        var ayStd: Double = 0.0
        var azStd: Double = 0.0

        let doPrint = true


        var timer =  Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { (timer) in


            if indexSpinup <= spinUpThreshold {
                if (doPrint){
                    print("Skipping measurement no: "+String(indexSpinup))
                }
                let idxObj = smapsMesId(mesid: Int(-5+indexSpinup))
                self.mesid?(idxObj)
                indexSpinup = indexSpinup + 1
            } else {
                //print(self.responses)
                // INITILIASATION
                if index == 1 {
                    if (doPrint){
                        print("Measurement no: "+String(index))
                    }
                    pressureAvg = self.responses["_p"]!
                    pressureStd = 0

                    altitudeAvg = self.responses["_altitude"]!
                    altitudeStd = 0
                    height = self.responses["_altitude"]!

                    verticalaccuracy = self.responses["_verticalAccuracy"]!
                    horisontalaccuracy = self.responses["_horizontalAccuracy"]!

                    speedAvg = self.responses["_speed"]!
                    speedStd = 0

                    axAvg = self.responses["_ax"]!
                    ayAvg = self.responses["_ay"]!
                    azAvg = self.responses["_az"]!
                    axStd = 0
                    ayStd = 0
                    azStd = 0

                } else {
                    if (doPrint){
                        print("Measurement no: "+String(index))
                    }
                    // Pressure
                    var _pressureAvg = (index - 1) / index * pressureAvg
                    _pressureAvg = _pressureAvg + (1 / index) * self.responses["_p"]!

                    var _pressureStd = (index - 1) / index * pow(pressureStd,2)
                    _pressureStd = _pressureStd + (1 / (index - 1) * pow((self.responses["_p"]! - _pressureAvg) , 2))
                    _pressureStd = sqrt(_pressureStd)
                    pressureAvg = _pressureAvg
                    pressureStd = _pressureStd


                    // Altitude
                    var _altitudeAvg = (index - 1) / index * altitudeAvg
                    _altitudeAvg = _altitudeAvg + (1 / index) * self.responses["_altitude"]!


                    var _altitudeStd = (index - 1) / index * pow(altitudeStd,2)
                    _altitudeStd = _altitudeStd + (1 / (index - 1) * pow((self.responses["_altitude"]! - _altitudeAvg) , 2))
                    _altitudeStd = sqrt(_altitudeStd)

                    altitudeAvg = _altitudeAvg
                    altitudeStd = _altitudeStd

                    height = self.responses["_altitude"]!
                    verticalaccuracy = self.responses["_verticalAccuracy"]!
                    horisontalaccuracy = self.responses["_horizontalAccuracy"]!


                    // Speed
                    var _speedAvg = (index - 1) / index * speedAvg
                    _speedAvg = _speedAvg + (1 / index) * self.responses["_speed"]!

                    var _speedStd = (index - 1) / index * pow(speedStd,2)
                    _speedStd = _speedStd + (1 / (index - 1) * pow((self.responses["_speed"]! - _speedAvg) , 2))
                    _speedStd = sqrt(_speedStd)
                    speedAvg = _speedAvg
                    speedStd = _speedStd


                    // X Acceleration
                    var _axAvg = (index - 1) / index * axAvg
                    _axAvg = _axAvg + (1 / index) * self.responses["_ax"]!
                    axAvg = _axAvg

                    var _axStd = (index - 1) / index * pow(axStd,2)
                    _axStd = _axStd + (1 / (index - 1) * pow((self.responses["_ax"]! - _axAvg),2))
                    _axStd = sqrt(_axStd)
                    axStd = _axStd


                    // Y Acceleration
                    var _ayAvg = (index - 1) / index * ayAvg
                    _ayAvg = _ayAvg + (1 / index) * self.responses["_ay"]!
                    ayAvg = _ayAvg

                    var _ayStd = (index - 1) / index * pow(ayStd,2)
                    _ayStd = _ayStd + (1 / (index - 1) * pow((self.responses["_ay"]! - _ayAvg),2))
                    _ayStd = sqrt(_ayStd)
                    ayStd = _ayStd


                    // Z Acceleration
                    var _azAvg = (index - 1) / index * azAvg
                    _azAvg = _azAvg + (1 / index) * self.responses["_az"]!
                    azAvg = _azAvg

                    var _azStd = (index - 1) / index * pow(azStd,2)
                    _azStd = _azStd + (1 / (index - 1) * pow((self.responses["_az"]! - _azAvg),2))
                    _azStd = sqrt(_azStd)
                    azStd = _azStd

                } // IF INDEX END

                let idxObj = smapsMesId(mesid: Int(index))
                self.mesid?(idxObj)
                index += 1


                // UPLOAD DATA
                if index == uploadThreshold+1 {
                    let timestamp = Int(Date().toMillis())

                    let (masked_lat, masked_lon) = maskLocation.mask_position(latitude: Double(self.lat)!, longitude: Double(self.lon)!)

                    let obsObj = smapsObsObj(timestamp: Int(timestamp),
                                             lat: masked_lat,
                                             lon: masked_lon,
                                             accelerationX: axAvg,
                                             accelerationY: ayAvg,
                                             accelerationZ: azAvg,
                                             accelerationXStd: axStd,
                                             accelerationYStd: ayStd,
                                             accelerationZStd: azStd,
                                             altidudeMean: altitudeAvg,
                                             altidudeStd: altitudeStd,
                                             horisontalAccuracy: horisontalaccuracy,
                                             pressureMean: pressureAvg,
                                             pressureStd: pressureStd,
                                             speedMean: speedAvg,
                                             speedStd: speedStd,
                                             verticalAccuracy:verticalaccuracy)

                    self.obscompleted?(obsObj)

                    if (doPrint) {
                     print("ready to upload")
                    }

                    index = 1
                }
            }
        } // Timer END

    } // smaps_start END

    public init(appKey: String) {
        self.lat = ""
        self.lon = ""
        self.altitude = 0
        self.speed = 0

        _appKey = appKey
        super.init()
    }

}
