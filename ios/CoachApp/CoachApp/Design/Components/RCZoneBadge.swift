import SwiftUI

struct RCZoneBadge: View {
    let zone: ZoneColor

    var body: some View {
        Text(zone.label)
            .font(.rcCaption(11))
            .fontWeight(.semibold)
            .foregroundStyle(zone.color)
            .padding(.horizontal, Spacing.sm)
            .padding(.vertical, Spacing.xs)
            .background(zone.color.opacity(0.12), in: Capsule())
    }
}

struct RCZoneDot: View {
    let zone: ZoneColor
    var size: CGFloat = 8

    var body: some View {
        Circle()
            .fill(zone.color)
            .frame(width: size, height: size)
    }
}

#Preview {
    HStack(spacing: 8) {
        ForEach([ZoneColor.recovery, .base, .threshold, .interval, .rest, .long], id: \.rawValue) { zone in
            RCZoneBadge(zone: zone)
        }
    }
    .padding()
}
