//
//  ContentView.swift
//  sampleSpoApp
//
//  Created by Kasper Hintz on 16/02/2022.
//

import SwiftUI
import smaps

class ExternalModel: ObservableObject {
    @Published var textToUpdate: String = "Update me!"
    func registerRequest() {
        // other functionality
        textToUpdate = "I've been updated!"
    }
}


func initiateSmaps(){
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

    }
    
}


struct UpdateContentView: View {
    @ObservedObject var viewModel: ExternalModel
    var body: some View {
        
        VStack(alignment: .leading) {
            HStack {
                Text("Latitude:")
                    .fontWeight(.semibold)
                    .padding()
                Text("NaN")
                    .padding()
            }
            
            HStack {
                Text("Longitude:")
                    .fontWeight(.semibold)
                    .padding()
                Text("NaN")
                    .padding()
            }
        }
       
        
    }
}



struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        UpdateContentView(viewModel: ExternalModel())
    }
}

//func startSmaps() {
//    let refObj = SpoLocation(appKey: "AppName")
//
//    fatalError("Smaps could not be started")
//}
