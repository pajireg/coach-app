import SwiftUI

struct TrendsView: View {
    var body: some View {
        NavigationStack {
            ZStack {
                AmbientBackground()
                VStack(spacing: Spacing.lg) {
                    Image(systemName: "chart.line.uptrend.xyaxis")
                        .font(.system(size: 64))
                        .foregroundStyle(.rcTextTertiary)
                    Text("준비 중")
                        .font(.rcTitle(28))
                        .foregroundStyle(.rcTextPrimary)
                    Text("훈련 추이 분석 기능을 곧 선보입니다")
                        .font(.rcBody())
                        .foregroundStyle(.rcTextSecondary)
                        .multilineTextAlignment(.center)
                }
                .padding(Spacing.xl)
            }
            .navigationTitle("추이")
            .navigationBarTitleDisplayMode(.large)
        }
    }
}

#Preview {
    TrendsView()
}
