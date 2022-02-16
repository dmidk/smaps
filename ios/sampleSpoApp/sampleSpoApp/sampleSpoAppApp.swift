//
//  sampleSpoAppApp.swift
//  sampleSpoApp
//
//  Created by Kasper Hintz on 16/02/2022.
//

import SwiftUI
import smaps

@main
struct sampleSpoAppApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    var body: some Scene {
        WindowGroup {
            UpdateContentView(viewModel: ExternalModel())
        }
        
    }
    
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        initiateSmaps()
//        let refObj = SpoLocation(appKey: "AppName")
//        refObj.smaps_start()
        
        return true
    }
}
