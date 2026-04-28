import Foundation

struct DashboardResponse: Decodable, Sendable {
    let user: User
    let schedule: Schedule
    let currentPlan: [PlannedWorkout]
    let recentActivities: [Activity]

    struct Schedule: Decodable, Sendable {
        let nextRunAt: Date?
        let lastRunAt: Date?
        let lastStatus: String?
        let lastError: String?
        let failureCount: Int
        let nextRetryAt: Date?
        let disabledAt: Date?
    }

    struct PlannedWorkout: Decodable, Sendable, Identifiable {
        let date: String
        let workoutName: String
        let sessionType: String
        let workoutType: String
        let plannedMinutes: Int
        let isRest: Bool

        var id: String { date + workoutName }

        var zoneColor: ZoneColor { ZoneColor(sessionType: sessionType) }
    }

    struct Activity: Decodable, Sendable, Identifiable {
        let provider: String
        let providerActivityId: String
        let activityDate: String
        let startedAt: Date?
        let title: String
        let sportType: String
        let distanceKm: Double?
        let durationSeconds: Int?
        let avgPace: String?
        let avgHr: Int?
        let plannedWorkoutName: String?
        let executionStatus: String?
        let executionQuality: String?
        let targetMatchScore: Double?

        var id: String { provider + providerActivityId }

        var formattedDuration: String? {
            guard let s = durationSeconds else { return nil }
            let m = s / 60
            return m >= 60 ? "\(m / 60)시간 \(m % 60)분" : "\(m)분"
        }
    }
}

enum ZoneColor: String, Sendable {
    case recovery, base, threshold, interval, rest, long

    init(sessionType: String) {
        self = switch sessionType.lowercased() {
        case "recovery": .recovery
        case "base": .base
        case "threshold": .threshold
        case "interval": .interval
        case "rest": .rest
        case "long": .long
        default: .base
        }
    }
}
