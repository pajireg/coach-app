import Foundation

struct IntegrationsResponse: Decodable, Sendable {
    let integrations: [Integration]
}

struct Integration: Decodable, Sendable, Identifiable {
    let provider: String
    let displayName: String
    let status: String
    let connected: Bool
    let source: String
    let capabilities: [String]
    let lastError: String?

    var id: String { provider }

    var isAttentionRequired: Bool {
        status == "reauth_required" || status == "error"
    }

    var isComingSoon: Bool { status == "coming_soon" }
}

struct FeedbackRequest: Encodable {
    let feedbackDate: String
    var fatigueScore: Int?
    var sorenessScore: Int?
    var stressScore: Int?
    var motivationScore: Int?
    var sleepQualityScore: Int?
    var painNotes: String?
    var notes: String?
}

struct GoalRequest: Encodable {
    let goalName: String
    let raceDate: String
    let distance: String
    var goalTime: String?
    var targetPace: String?
    var priority: Int?
    var isActive: Bool?
}

struct AvailabilityRequest: Encodable {
    let weekday: Int
    let isAvailable: Bool
    var maxDurationMinutes: Int?
    var preferredSessionType: String?
}

struct InjuryRequest: Encodable {
    let statusDate: String
    let injuryArea: String
    let severity: Int
    var notes: String?
    var isActive: Bool?
}
