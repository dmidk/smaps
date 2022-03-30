//
//  mask_location.swift
//  smaps
//
//  Created by Kasper Hintz on 25/05/2021.
//  Copyright © 2021 Kasper Hintz. All rights reserved.
//

import Foundation

public class mask_location {

    private func degreesToRadians(degrees: Double) -> Double {
        return degrees * Double.pi / 180.0
    }

    private func radiansToDegrees(radians: Double) -> Double {
        return radians * 180.0 / Double.pi
    }
    

    private func random(min: Int, max: Int) -> Int {
        return Int(arc4random_uniform(UInt32(max - min + 1))) + min
    }
    
    
    private func positional_noise(latitude: Double, longitude: Double) -> (Double, Double){
        
        let R = 6371000.0
        let D = 150 // Noise in meters. Change this value for more or less noise to the position
        
        let d = random(min: 0, max: D)
        let bearing = degreesToRadians(degrees: Double(random(min: 0, max: 360)))
        
        let delta = Double(d)/R
        
        let phi1 = degreesToRadians(degrees: latitude)
        let lambda1 = degreesToRadians(degrees: longitude)
        
        let phi2 = asin( sin(phi1) * cos(delta) + cos(phi1) * sin(delta) * cos(bearing) )
        let lambda2 = lambda1 + atan2( sin(bearing) * sin(delta) * cos(phi1), cos(delta)-sin(phi1)*sin(phi2))
        
        let masked_lat = radiansToDegrees(radians: phi2)
        let masked_lon = radiansToDegrees(radians: lambda2)
        
        return (masked_lat, masked_lon)
    }


    public func mask_position(latitude: Double, longitude: Double) -> (Double, Double) {
        let (masked_lat, masked_lon) = positional_noise(latitude: latitude, longitude: longitude)
        
        return (masked_lat, masked_lon)
    }

}
