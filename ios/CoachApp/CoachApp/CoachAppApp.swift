import SwiftUI

@main
struct CoachAppApp: App {
    @State private var authStore = AuthStore()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environment(authStore)
        }
    }
}
