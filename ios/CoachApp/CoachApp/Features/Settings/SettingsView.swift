import SwiftUI

@Observable
final class SettingsViewModel {
    var user: User?
    var integrations: [Integration] = []
    var isLoading = false
    var isSaving = false
    var error: Error?

    var displayName = ""
    var garminEmail = ""
    var timezone = "Asia/Seoul"
    var runMode = "auto"
    var includeStrength = false

    func load(client: APIClient) async {
        isLoading = true
        async let userResult = client.getMe()
        async let intResult = client.getIntegrations()
        do {
            let (u, i) = try await (userResult, intResult)
            user = u
            integrations = i.integrations
            displayName = u.displayName
            garminEmail = u.garminEmail ?? ""
            timezone = u.preferences.timezone
            runMode = u.preferences.runMode
            includeStrength = u.preferences.includeStrength
        } catch {
            self.error = error
        }
        isLoading = false
    }

    func save(client: APIClient) async {
        isSaving = true
        let req = UpdatePreferencesRequest(
            displayName: displayName,
            garminEmail: garminEmail.isEmpty ? nil : garminEmail,
            timezone: timezone,
            runMode: runMode,
            includeStrength: includeStrength
        )
        do {
            let updated = try await client.updatePreferences(req)
            user = updated
        } catch {
            self.error = error
        }
        isSaving = false
    }
}

struct SettingsView: View {
    @Environment(APIClient.self) private var client
    @Environment(AuthStore.self) private var authStore
    @State private var viewModel = SettingsViewModel()

    var body: some View {
        NavigationStack {
            ZStack {
                AmbientBackground()
                List {
                    profileSection
                    preferencesSection
                    integrationsSection
                    devSection
                }
                .scrollContentBackground(.hidden)
            }
            .navigationTitle("설정")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    if viewModel.isSaving {
                        ProgressView()
                    } else {
                        Button("저장") {
                            Task { await viewModel.save(client: client) }
                        }
                        .fontWeight(.semibold)
                    }
                }
            }
        }
        .task { await viewModel.load(client: client) }
    }

    private var profileSection: some View {
        Section("프로필") {
            HStack {
                Text("이름")
                Spacer()
                TextField("Runner", text: $viewModel.displayName)
                    .multilineTextAlignment(.trailing)
                    .foregroundStyle(.rcTextSecondary)
            }
            HStack {
                Text("가민 이메일")
                Spacer()
                TextField("email@example.com", text: $viewModel.garminEmail)
                    .multilineTextAlignment(.trailing)
                    .foregroundStyle(.rcTextSecondary)
                    .keyboardType(.emailAddress)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)
            }
        }
    }

    private var preferencesSection: some View {
        Section("훈련 설정") {
            Picker("러닝 모드", selection: $viewModel.runMode) {
                Text("자동").tag("auto")
                Text("계획").tag("plan")
            }
            Toggle("근력 훈련 포함", isOn: $viewModel.includeStrength)
            HStack {
                Text("시간대")
                Spacer()
                Text(viewModel.timezone)
                    .foregroundStyle(.rcTextSecondary)
            }
        }
    }

    private var integrationsSection: some View {
        Section("연동") {
            ForEach(viewModel.integrations) { integration in
                IntegrationRow(integration: integration)
            }
        }
    }

    private var devSection: some View {
        Section("개발자") {
            Button(role: .destructive) {
                authStore.signOut()
            } label: {
                Text("로그아웃 (API 키 삭제)")
            }
        }
    }
}

struct IntegrationRow: View {
    let integration: Integration

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(integration.displayName)
                    .font(.rcBody())
                if integration.isComingSoon {
                    Text("준비 중")
                        .font(.rcCaption(11))
                        .foregroundStyle(.rcTextTertiary)
                }
            }
            Spacer()
            if integration.isAttentionRequired {
                Image(systemName: "exclamationmark.circle.fill")
                    .foregroundStyle(.zoneInterval)
            } else if integration.isComingSoon {
                Image(systemName: "clock")
                    .foregroundStyle(.rcTextTertiary)
            } else if integration.connected {
                Image(systemName: "checkmark.circle.fill")
                    .foregroundStyle(.zoneBase)
            } else {
                Image(systemName: "circle")
                    .foregroundStyle(.rcTextTertiary)
            }
        }
    }
}

#Preview {
    SettingsView()
        .environment(APIClient(baseURL: "http://localhost:8000", apiKey: "preview"))
        .environment(AuthStore())
}
