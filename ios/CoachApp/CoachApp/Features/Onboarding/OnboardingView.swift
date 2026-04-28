import SwiftUI

struct OnboardingView: View {
    @Environment(AuthStore.self) private var authStore

    @State private var apiKey = ""
    @State private var baseURL = "http://localhost:8000"
    @State private var isLoading = false
    @State private var errorMessage: String?

    var body: some View {
        ZStack {
            AmbientBackground()
            ScrollView {
                VStack(spacing: Spacing.xl) {
                    header
                    inputFields
                    if let error = errorMessage {
                        Text(error)
                            .font(.rcCaption())
                            .foregroundStyle(.red)
                            .multilineTextAlignment(.center)
                    }
                    RCButton("시작하기", isLoading: isLoading) { connect() }
                        .disabled(apiKey.isEmpty)
                }
                .padding(Spacing.lg)
                .padding(.top, 80)
            }
        }
    }

    private var header: some View {
        VStack(spacing: Spacing.sm) {
            Image(systemName: "figure.run.circle.fill")
                .font(.system(size: 64))
                .foregroundStyle(.rcAccent)
            Text("Running Coach")
                .font(.rcTitle(32))
                .foregroundStyle(.rcTextPrimary)
            Text("API 키를 입력해 연결해주세요")
                .font(.rcBody())
                .foregroundStyle(.rcTextSecondary)
        }
    }

    private var inputFields: some View {
        VStack(spacing: Spacing.md) {
            RCCard {
                VStack(alignment: .leading, spacing: Spacing.sm) {
                    Text("서버 주소")
                        .font(.rcCaption())
                        .foregroundStyle(.rcTextSecondary)
                    TextField("http://localhost:8000", text: $baseURL)
                        .font(.rcMono(14))
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .keyboardType(.URL)
                }
                .padding(Spacing.md)
            }
            RCCard {
                VStack(alignment: .leading, spacing: Spacing.sm) {
                    Text("API 키")
                        .font(.rcCaption())
                        .foregroundStyle(.rcTextSecondary)
                    SecureField("rcu_...", text: $apiKey)
                        .font(.rcMono(14))
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                }
                .padding(Spacing.md)
            }
        }
    }

    private func connect() {
        guard !apiKey.isEmpty else { return }
        isLoading = true
        errorMessage = nil
        Task {
            do {
                try authStore.save(apiKey: apiKey.trimmingCharacters(in: .whitespaces),
                                   baseURL: baseURL.trimmingCharacters(in: .whitespaces))
            } catch {
                errorMessage = error.localizedDescription
            }
            isLoading = false
        }
    }
}

#Preview {
    OnboardingView()
        .environment(AuthStore())
}
