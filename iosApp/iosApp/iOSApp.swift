import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        onCreateApp()
    }
    
	var body: some Scene {
		WindowGroup {
		    ZStack {
		        //Color.white.ignoresSafeArea(.all) // status bar color
			    ContentView()
			}
		}
	}
}
