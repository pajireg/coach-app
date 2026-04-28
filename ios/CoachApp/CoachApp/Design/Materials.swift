import SwiftUI

// MARK: - Liquid Glass Card

struct RCCard<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        content
            .background {
                RoundedRectangle(cornerRadius: Radius.lg)
                    .fill(.regularMaterial)
                    .overlay {
                        RoundedRectangle(cornerRadius: Radius.lg)
                            .strokeBorder(
                                LinearGradient(
                                    colors: [.white.opacity(0.5), .white.opacity(0.1)],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                ),
                                lineWidth: 0.5
                            )
                    }
            }
    }
}

// MARK: - Ambient Background

struct AmbientBackground: View {
    var body: some View {
        ZStack {
            Color(.systemGroupedBackground)
                .ignoresSafeArea()
            // Subtle green wash in top-right
            Circle()
                .fill(Color.zoneBase.opacity(0.08))
                .frame(width: 320, height: 320)
                .blur(radius: 60)
                .offset(x: 100, y: -120)
                .allowsHitTesting(false)
        }
        .ignoresSafeArea()
    }
}
