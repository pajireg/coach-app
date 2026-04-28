import SwiftUI

struct WeeklyView: View {
    @Environment(APIClient.self) private var client
    @State private var dashboard: DashboardResponse?
    @State private var isLoading = false

    private let weekdays = ["월", "화", "수", "목", "금", "토", "일"]

    var body: some View {
        NavigationStack {
            ZStack {
                AmbientBackground()
                content
            }
            .navigationTitle("주간")
            .navigationBarTitleDisplayMode(.large)
        }
        .task {
            guard dashboard == nil else { return }
            isLoading = true
            dashboard = try? await client.getDashboard()
            isLoading = false
        }
    }

    @ViewBuilder
    private var content: some View {
        if isLoading {
            ProgressView("불러오는 중…")
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            ScrollView {
                LazyVStack(spacing: Spacing.md) {
                    weekCalendar
                    planList
                }
                .padding(Spacing.md)
                .padding(.bottom, 100)
            }
        }
    }

    private var weekCalendar: some View {
        RCCard {
            HStack(spacing: 0) {
                ForEach(Array(weekdays.enumerated()), id: \.offset) { index, day in
                    let plan = planForWeekday(index)
                    VStack(spacing: Spacing.xs) {
                        Text(day)
                            .font(.rcCaption(11))
                            .foregroundStyle(.rcTextTertiary)
                        ZStack {
                            Circle()
                                .fill(plan.map { $0.zoneColor.color.opacity(0.15) } ?? Color.clear)
                                .frame(width: 36, height: 36)
                            if let plan {
                                RCZoneDot(zone: plan.zoneColor, size: 8)
                            } else {
                                Text(dayNumber(offset: index))
                                    .font(.rcCaption(12))
                                    .foregroundStyle(.rcTextTertiary)
                            }
                        }
                    }
                    .frame(maxWidth: .infinity)
                }
            }
            .padding(Spacing.md)
        }
    }

    private var planList: some View {
        VStack(alignment: .leading, spacing: Spacing.sm) {
            if let plan = dashboard?.currentPlan, !plan.isEmpty {
                Text("이번 주 계획")
                    .font(.rcHeadline())
                    .foregroundStyle(.rcTextPrimary)
                    .padding(.horizontal, Spacing.xs)
                ForEach(plan) { workout in
                    WorkoutRow(workout: workout)
                }
            } else {
                emptyState
            }
        }
    }

    private var emptyState: some View {
        VStack(spacing: Spacing.md) {
            Image(systemName: "calendar")
                .font(.system(size: 48))
                .foregroundStyle(.rcTextTertiary)
            Text("이번 주 계획이 없습니다")
                .font(.rcHeadline())
                .foregroundStyle(.rcTextSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, Spacing.xxl)
    }

    private func planForWeekday(_ index: Int) -> DashboardResponse.PlannedWorkout? {
        dashboard?.currentPlan.first { workout in
            guard let date = ISO8601DateFormatter().date(from: workout.date + "T00:00:00Z") else { return false }
            let weekday = Calendar.current.component(.weekday, from: date)
            let mappedIndex = (weekday + 5) % 7
            return mappedIndex == index
        }
    }

    private func dayNumber(offset: Int) -> String {
        let today = Calendar.current.startOfDay(for: Date())
        let weekday = Calendar.current.component(.weekday, from: today)
        let mondayOffset = (weekday + 5) % 7
        let targetDate = Calendar.current.date(byAdding: .day, value: offset - mondayOffset, to: today) ?? today
        return "\(Calendar.current.component(.day, from: targetDate))"
    }
}

#Preview {
    WeeklyView()
        .environment(APIClient(baseURL: "http://localhost:8000", apiKey: "preview"))
}
