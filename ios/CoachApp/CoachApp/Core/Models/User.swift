import Foundation

struct User: Decodable, Sendable {
    let userId: String
    let externalKey: String
    let displayName: String
    let garminEmail: String?
    let preferences: Preferences
    let integrationStatus: IntegrationStatus

    struct Preferences: Decodable, Sendable {
        let timezone: String
        let locale: String
        let scheduleTimes: String
        let runMode: String
        let includeStrength: Bool
    }

    struct IntegrationStatus: Decodable, Sendable {
        let garmin: String?
        let googleCalendar: String?
    }
}

struct UpdatePreferencesRequest: Encodable {
    var displayName: String?
    var garminEmail: String?
    var timezone: String?
    var locale: String?
    var scheduleTimes: String?
    var runMode: String?
    var includeStrength: Bool?
}
