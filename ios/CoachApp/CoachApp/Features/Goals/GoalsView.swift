import SwiftUI

struct GoalsView: View {
    @Environment(APIClient.self) private var client

    @State private var goalName = ""
    @State private var raceDate = Date()
    @State private var distance = "10K"
    @State private var goalTime = ""
    @State private var isLoading = false
    @State private var successMessage: String?
    @State private var errorMessage: String?

    private let distances = ["5K", "10K", "하프마라톤", "마라톤", "기타"]

    var body: some View {
        NavigationStack {
            ZStack {
                AmbientBackground()
                ScrollView {
                    VStack(spacing: Spacing.md) {
                        RCCard {
                            VStack(alignment: .leading, spacing: Spacing.md) {
                                field("목표 이름", text: $goalName, placeholder: "예: 10K PB")
                                Divider()
                                VStack(alignment: .leading, spacing: Spacing.xs) {
                                    Text("거리")
                                        .font(.rcCaption())
                                        .foregroundStyle(.rcTextSecondary)
                                    Picker("거리", selection: $distance) {
                                        ForEach(distances, id: \.self) { Text($0) }
                                    }
                                    .pickerStyle(.segmented)
                                }
                                Divider()
                                VStack(alignment: .leading, spacing: Spacing.xs) {
                                    Text("레이스 날짜")
                                        .font(.rcCaption())
                                        .foregroundStyle(.rcTextSecondary)
                                    DatePicker("", selection: $raceDate, displayedComponents: .date)
                                        .labelsHidden()
                                }
                                Divider()
                                field("목표 기록 (선택)", text: $goalTime, placeholder: "49:00")
                            }
                            .padding(Spacing.md)
                        }

                        if let msg = successMessage {
                            Label(msg, systemImage: "checkmark.circle.fill")
                                .font(.rcBody())
                                .foregroundStyle(.zoneBase)
                        }
                        if let msg = errorMessage {
                            Label(msg, systemImage: "exclamationmark.circle")
                                .font(.rcBody())
                                .foregroundStyle(.zoneInterval)
                        }

                        RCButton("목표 저장", isLoading: isLoading) { submit() }
                            .disabled(goalName.isEmpty)
                    }
                    .padding(Spacing.md)
                    .padding(.bottom, 100)
                }
            }
            .navigationTitle("목표")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    private func field(_ label: String, text: Binding<String>, placeholder: String) -> some View {
        VStack(alignment: .leading, spacing: Spacing.xs) {
            Text(label)
                .font(.rcCaption())
                .foregroundStyle(.rcTextSecondary)
            TextField(placeholder, text: text)
                .font(.rcBody())
        }
    }

    private func submit() {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let dateStr = formatter.string(from: raceDate)
        let req = GoalRequest(
            goalName: goalName,
            raceDate: dateStr,
            distance: distance,
            goalTime: goalTime.isEmpty ? nil : goalTime,
            isActive: true
        )
        isLoading = true
        successMessage = nil
        errorMessage = nil
        Task {
            do {
                _ = try await client.submitGoal(req)
                successMessage = "목표가 저장되었습니다!"
            } catch {
                errorMessage = error.localizedDescription
            }
            isLoading = false
        }
    }
}

#Preview {
    GoalsView()
        .environment(APIClient(baseURL: "http://localhost:8000", apiKey: "preview"))
}
