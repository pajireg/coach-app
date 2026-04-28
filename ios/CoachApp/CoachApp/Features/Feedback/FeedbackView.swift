import SwiftUI

struct FeedbackView: View {
    @Environment(APIClient.self) private var client
    @Environment(\.dismiss) private var dismiss

    @State private var fatigue = 5.0
    @State private var soreness = 5.0
    @State private var stress = 5.0
    @State private var motivation = 5.0
    @State private var sleepQuality = 5.0
    @State private var notes = ""
    @State private var isLoading = false
    @State private var didSubmit = false
    @State private var errorMessage: String?

    var body: some View {
        NavigationStack {
            ZStack {
                AmbientBackground()
                ScrollView {
                    VStack(spacing: Spacing.md) {
                        RCCard {
                            VStack(spacing: Spacing.lg) {
                                slider("피로도", value: $fatigue, lowLabel: "상쾌", highLabel: "매우 피곤")
                                Divider()
                                slider("근육통", value: $soreness, lowLabel: "없음", highLabel: "심함")
                                Divider()
                                slider("스트레스", value: $stress, lowLabel: "낮음", highLabel: "높음")
                                Divider()
                                slider("동기부여", value: $motivation, lowLabel: "낮음", highLabel: "높음")
                                Divider()
                                slider("수면 질", value: $sleepQuality, lowLabel: "나쁨", highLabel: "좋음")
                            }
                            .padding(Spacing.md)
                        }

                        RCCard {
                            VStack(alignment: .leading, spacing: Spacing.sm) {
                                Text("메모 (선택)")
                                    .font(.rcCaption())
                                    .foregroundStyle(.rcTextSecondary)
                                TextField("오늘 컨디션에 대해 자유롭게 적어주세요", text: $notes, axis: .vertical)
                                    .font(.rcBody())
                                    .lineLimit(3...6)
                            }
                            .padding(Spacing.md)
                        }

                        if let error = errorMessage {
                            Label(error, systemImage: "exclamationmark.circle")
                                .font(.rcCaption())
                                .foregroundStyle(.zoneInterval)
                        }

                        if didSubmit {
                            Label("피드백이 제출되었습니다!", systemImage: "checkmark.circle.fill")
                                .font(.rcBody())
                                .foregroundStyle(.zoneBase)
                        }

                        RCButton("피드백 제출", isLoading: isLoading) { submit() }
                    }
                    .padding(Spacing.md)
                    .padding(.bottom, 32)
                }
            }
            .navigationTitle("오늘의 컨디션")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("닫기") { dismiss() }
                }
            }
        }
    }

    private func slider(_ label: String, value: Binding<Double>, lowLabel: String, highLabel: String) -> some View {
        VStack(alignment: .leading, spacing: Spacing.xs) {
            HStack {
                Text(label)
                    .font(.rcHeadline(14))
                    .foregroundStyle(.rcTextPrimary)
                Spacer()
                Text("\(Int(value.wrappedValue))")
                    .font(.rcMono(14))
                    .foregroundStyle(.rcAccent)
            }
            Slider(value: value, in: 1...10, step: 1)
                .tint(.rcAccent)
            HStack {
                Text(lowLabel)
                    .font(.rcCaption(11))
                    .foregroundStyle(.rcTextTertiary)
                Spacer()
                Text(highLabel)
                    .font(.rcCaption(11))
                    .foregroundStyle(.rcTextTertiary)
            }
        }
    }

    private func submit() {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        let today = formatter.string(from: Date())
        let req = FeedbackRequest(
            feedbackDate: today,
            fatigueScore: Int(fatigue),
            sorenessScore: Int(soreness),
            stressScore: Int(stress),
            motivationScore: Int(motivation),
            sleepQualityScore: Int(sleepQuality),
            notes: notes.isEmpty ? nil : notes
        )
        isLoading = true
        errorMessage = nil
        Task {
            do {
                _ = try await client.submitFeedback(req)
                didSubmit = true
            } catch {
                errorMessage = error.localizedDescription
            }
            isLoading = false
        }
    }
}

#Preview {
    FeedbackView()
        .environment(APIClient(baseURL: "http://localhost:8000", apiKey: "preview"))
}
