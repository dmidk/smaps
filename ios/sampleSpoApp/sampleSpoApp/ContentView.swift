//
//  ContentView.swift
//  sampleSpoApp
//
//  Created by Kasper Hintz on 16/02/2022.
//

import SwiftUI
import smaps


class startSmaps: ObservableObject {
    @Published var textLatitude: String = "---"
    @Published var textLongitude: String = "---"
    @Published var textPressure: String = "---"

    
    init() {
        
        let refObj = SpoLocation(appKey: "AppName")
        refObj.smaps_start()

        // Triggered when new observation is ready
        refObj.obscompleted = {obscompleted in

        var obsdict : [String: Any] {
                return ["speedMean:": obscompleted.self.speedMean,
                        "speedStd": obscompleted.self.speedStd,
                        "latitude": obscompleted.self.lat,
                        "longitude": obscompleted.self.lon,
                        "horizontalAccuracy": obscompleted.self.horisontalAccuracy,
                        "altitudeMean": obscompleted.self.altidudeMean,
                        "altidudeStd": obscompleted.self.altidudeStd,
                        "verticalAccuracy": obscompleted.self.verticalAccuracy,
                        "pressureMean": obscompleted.self.pressureMean,
                        "pressureStd": obscompleted.self.pressureStd,
                        "timestamp": obscompleted.self.timestamp,
                        "ax": obscompleted.self.accelerationX,
                        "ay": obscompleted.self.accelerationY,
                        "az": obscompleted.self.accelerationZ,
                        "axStd": obscompleted.self.accelerationXStd,
                        "ayStd": obscompleted.self.accelerationYStd,
                        "azStd": obscompleted.self.accelerationZStd]
                 }
            
        self.textLatitude = String(round(obscompleted.self.lat*1000)/1000.0)
        self.textLongitude = String(round(obscompleted.self.lon*1000)/1000.0)
        self.textPressure = String(round(obscompleted.self.pressureMean*1000)/1000.0)
            
        }
    }
}



struct UpdateContentView: View {
    
    @ObservedObject var viewSmaps: startSmaps = startSmaps()


    var body: some View {
        
        VStack(alignment: .leading, spacing: 30) {
            Label("SampleSpoApp", systemImage: "cloud.rain")
        
        VStack(alignment: .leading, spacing: -10.0) {
            HStack {
                Text("Latitude:")
                    .fontWeight(.semibold)
                    .padding()
                Text(viewSmaps.textLatitude)
                    .padding()
            }
            HStack {
                Text("Longitude:")
                    .fontWeight(.semibold)
                    .padding()
                Text(viewSmaps.textLongitude)
                    .padding()
            }
            HStack {
                Text("Pressure:")
                    .fontWeight(.semibold)
                    .padding()
                Text(viewSmaps.textPressure)
                    .padding()
                Text("hPa")
                    .padding()
            }
        }.padding()
            
        }
        

    }
}



struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        UpdateContentView()
            .previewInterfaceOrientation(.portrait)
    }
}
