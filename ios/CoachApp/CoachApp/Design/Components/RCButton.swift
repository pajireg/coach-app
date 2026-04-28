import SwiftUI

struct RCButton: View {
    enum Style { case primary, secondary, ghost }

    let title: String
    let style: Style
    let isLoading: Bool
    let action: () -> Void

    init(_ title: String, style: Style = .primary, isLoading: Bool = false, action: @escaping () -> Void) {
        self.title = title
        self.style = style
        self.isLoading = isLoading
        self.action = action
    }

    var body: some View {
        Button(action: action) {
            ZStack {
                label
                    .opacity(isLoading ? 0 : 1)
                if isLoading {
                    ProgressView()
                        .tint(style == .primary ? .white : .rcAccent)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 52)
            .background(background)
            .clipShape(RoundedRectangle(cornerRadius: Radius.md))
            .overlay {
                if style == .secondary {
                    RoundedRectangle(cornerRadius: Radius.md)
                        .strokeBorder(Color.rcAccent, lineWidth: 1.5)
                }
            }
        }
        .disabled(isLoading)
    }

    @ViewBuilder
    private var label: some View {
        Text(title)
            .font(.rcHeadline())
            .foregroundStyle(style == .primary ? .white : style == .secondary ? .rcAccent : .rcTextPrimary)
    }

    @ViewBuilder
    private var background: some View {
        switch style {
        case .primary: Color.rcAccent
        case .secondary: Color.clear
        case .ghost: Color.clear
        }
    }
}

#Preview {
    VStack(spacing: 12) {
        RCButton("훈련 시작") {}
        RCButton("나중에", style: .secondary) {}
        RCButton("건너뛰기", style: .ghost) {}
        RCButton("로딩 중", isLoading: true) {}
    }
    .padding()
}
