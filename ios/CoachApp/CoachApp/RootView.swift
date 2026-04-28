import SwiftUI

struct RootView: View {
    @Environment(AuthStore.self) private var authStore

    var body: some View {
        if authStore.isAuthenticated, let apiKey = authStore.apiKey {
            MainTabView()
                .environment(APIClient(baseURL: authStore.baseURL, apiKey: apiKey))
        } else {
            OnboardingView()
        }
    }
}

struct MainTabView: View {
    @State private var showFeedback = false

    var body: some View {
        TabView {
            Tab("오늘", systemImage: "sun.max") {
                HomeView()
            }
            Tab("주간", systemImage: "calendar") {
                WeeklyView()
            }
            Tab("추이", systemImage: "chart.line.uptrend.xyaxis") {
                TrendsView()
            }
            Tab("목표", systemImage: "flag") {
                GoalsView()
            }
            Tab("설정", systemImage: "gearshape") {
                SettingsView()
            }
        }
        .sheet(isPresented: $showFeedback) {
            FeedbackView()
        }
    }
}

#Preview {
    RootView()
        .environment(AuthStore())
}
