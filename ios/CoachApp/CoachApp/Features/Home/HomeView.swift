import SwiftUI

@Observable
final class HomeViewModel {
    var dashboard: DashboardResponse?
    var isLoading = false
    var error: Error?

    func load(client: APIClient) async {
        isLoading = true
        error = nil
        do {
            dashboard = try await client.getDashboard()
        } catch {
            self.error = error
        }
        isLoading = false
    }
}

struct HomeView: View {
    @Environment(APIClient.self) private var client
    @State private var viewModel = HomeViewModel()

    var body: some View {
        NavigationStack {
            ZStack {
                AmbientBackground()
                content
            }
            .navigationTitle("오늘")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        Task { await viewModel.load(client: client) }
                    } label: {
                        Image(systemName: "arrow.clockwise")
                    }
                }
            }
        }
        .task { await viewModel.load(client: client) }
    }

    @ViewBuilder
    private var content: some View {
        if viewModel.isLoading && viewModel.dashboard == nil {
            ProgressView("불러오는 중…")
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if let dashboard = viewModel.dashboard {
            ScrollView {
                LazyVStack(spacing: Spacing.md) {
                    if !dashboard.currentPlan.isEmpty {
                        planSection(dashboard.currentPlan)
                    }
                    if !dashboard.recentActivities.isEmpty {
                        activitiesSection(dashboard.recentActivities)
                    }
                    if dashboard.currentPlan.isEmpty && dashboard.recentActivities.isEmpty {
                        emptyState
                    }
                }
                .padding(Spacing.md)
                .padding(.bottom, 100)
            }
        } else if let error = viewModel.error {
            errorView(error)
        } else {
            emptyState
        }
    }

    private func planSection(_ plan: [DashboardResponse.PlannedWorkout]) -> some View {
        VStack(alignment: .leading, spacing: Spacing.sm) {
            Text("이번 주 훈련")
                .font(.rcHeadline())
                .foregroundStyle(.rcTextPrimary)
                .padding(.horizontal, Spacing.xs)
            ForEach(plan) { workout in
                WorkoutRow(workout: workout)
            }
        }
    }

    private func activitiesSection(_ activities: [DashboardResponse.Activity]) -> some View {
        VStack(alignment: .leading, spacing: Spacing.sm) {
            Text("최근 활동")
                .font(.rcHeadline())
                .foregroundStyle(.rcTextPrimary)
                .padding(.horizontal, Spacing.xs)
            ForEach(activities) { activity in
                ActivityRow(activity: activity)
            }
        }
    }

    private var emptyState: some View {
        VStack(spacing: Spacing.md) {
            Image(systemName: "figure.run")
                .font(.system(size: 48))
                .foregroundStyle(.rcTextTertiary)
            Text("훈련 계획이 없습니다")
                .font(.rcHeadline())
                .foregroundStyle(.rcTextSecondary)
            Text("코치가 곧 계획을 만들어드릴게요")
                .font(.rcBody())
                .foregroundStyle(.rcTextTertiary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, Spacing.xxl)
    }

    private func errorView(_ error: Error) -> some View {
        VStack(spacing: Spacing.md) {
            Image(systemName: "exclamationmark.triangle")
                .font(.system(size: 48))
                .foregroundStyle(.rcTextTertiary)
            Text(error.localizedDescription)
                .font(.rcBody())
                .foregroundStyle(.rcTextSecondary)
                .multilineTextAlignment(.center)
            RCButton("다시 시도", style: .secondary) {
                Task { await viewModel.load(client: client) }
            }
        }
        .padding(Spacing.lg)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Sub-views

struct WorkoutRow: View {
    let workout: DashboardResponse.PlannedWorkout

    var body: some View {
        RCCard {
            HStack(spacing: Spacing.md) {
                RCZoneDot(zone: workout.zoneColor, size: 10)
                VStack(alignment: .leading, spacing: Spacing.xs) {
                    Text(workout.workoutName)
                        .font(.rcHeadline(15))
                        .foregroundStyle(.rcTextPrimary)
                    Text(workout.date)
                        .font(.rcCaption())
                        .foregroundStyle(.rcTextTertiary)
                }
                Spacer()
                if !workout.isRest {
                    Text("\(workout.plannedMinutes)분")
                        .font(.rcMono(14))
                        .foregroundStyle(.rcTextSecondary)
                }
                RCZoneBadge(zone: workout.zoneColor)
            }
            .padding(Spacing.md)
        }
    }
}

struct ActivityRow: View {
    let activity: DashboardResponse.Activity

    var body: some View {
        RCCard {
            HStack(spacing: Spacing.md) {
                Image(systemName: "figure.run")
                    .font(.system(size: 20))
                    .foregroundStyle(.rcAccent)
                    .frame(width: 36, height: 36)
                    .background(Color.rcAccent.opacity(0.1), in: Circle())
                VStack(alignment: .leading, spacing: Spacing.xs) {
                    Text(activity.title)
                        .font(.rcHeadline(15))
                        .foregroundStyle(.rcTextPrimary)
                    HStack(spacing: Spacing.sm) {
                        if let dist = activity.distanceKm {
                            Text(String(format: "%.1fkm", dist))
                                .font(.rcMono(13))
                                .foregroundStyle(.rcTextSecondary)
                        }
                        if let pace = activity.avgPace {
                            Text(pace + "/km")
                                .font(.rcMono(13))
                                .foregroundStyle(.rcTextSecondary)
                        }
                    }
                }
                Spacer()
                if let duration = activity.formattedDuration {
                    Text(duration)
                        .font(.rcCaption())
                        .foregroundStyle(.rcTextTertiary)
                }
            }
            .padding(Spacing.md)
        }
    }
}

#Preview {
    HomeView()
        .environment(APIClient(baseURL: "http://localhost:8000", apiKey: "preview"))
}
