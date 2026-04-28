import Foundation
import Observation

@Observable
final class APIClient: @unchecked Sendable {
    private let baseURL: String
    private let apiKey: String
    private let session: URLSession
    private let decoder: JSONDecoder

    init(baseURL: String, apiKey: String) {
        self.baseURL = baseURL.hasSuffix("/") ? String(baseURL.dropLast()) : baseURL
        self.apiKey = apiKey
        self.session = URLSession.shared
        let d = JSONDecoder()
        d.keyDecodingStrategy = .convertFromSnakeCase
        d.dateDecodingStrategy = .iso8601
        self.decoder = d
    }

    // MARK: - User

    func getMe() async throws -> User {
        try await get("/v1/me")
    }

    func getDashboard() async throws -> DashboardResponse {
        try await get("/v1/me/dashboard")
    }

    func getIntegrations() async throws -> IntegrationsResponse {
        try await get("/v1/me/integrations")
    }

    func updatePreferences(_ body: UpdatePreferencesRequest) async throws -> User {
        try await patch("/v1/me/preferences", body: body)
    }

    // MARK: - Coaching Inputs

    func submitFeedback(_ body: FeedbackRequest) async throws -> EmptyResponse {
        try await post("/v1/me/feedback", body: body)
    }

    func submitGoal(_ body: GoalRequest) async throws -> EmptyResponse {
        try await post("/v1/me/goals", body: body)
    }

    func submitAvailability(_ body: AvailabilityRequest) async throws -> EmptyResponse {
        try await post("/v1/me/availability", body: body)
    }

    func submitInjury(_ body: InjuryRequest) async throws -> EmptyResponse {
        try await post("/v1/me/injuries", body: body)
    }

    // MARK: - Dev

    func triggerSync(mode: String = "auto") async throws -> EmptyResponse {
        try await post("/v1/runs/sync", body: SyncRequest(mode: mode))
    }

    // MARK: - Private

    private func request(method: String, path: String, body: (any Encodable)? = nil) throws -> URLRequest {
        guard let url = URL(string: baseURL + path) else { throw APIError.invalidURL }
        var req = URLRequest(url: url)
        req.httpMethod = method
        req.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
        req.setValue("application/json", forHTTPHeaderField: "Accept")
        if let body {
            req.setValue("application/json", forHTTPHeaderField: "Content-Type")
            req.httpBody = try JSONEncoder().encode(body)
        }
        return req
    }

    private func perform<T: Decodable>(_ req: URLRequest) async throws -> T {
        let data: Data
        let response: URLResponse
        do {
            (data, response) = try await session.data(for: req)
        } catch {
            throw APIError.networkFailed(error)
        }
        if let http = response as? HTTPURLResponse {
            switch http.statusCode {
            case 200...299: break
            case 401: throw APIError.unauthorized
            case 404: throw APIError.notFound
            default: throw APIError.serverError(http.statusCode)
            }
        }
        do {
            return try decoder.decode(T.self, from: data)
        } catch {
            throw APIError.decodingFailed(error)
        }
    }

    private func get<T: Decodable>(_ path: String) async throws -> T {
        let req = try request(method: "GET", path: path)
        return try await perform(req)
    }

    private func post<T: Decodable>(_ path: String, body: some Encodable) async throws -> T {
        let req = try request(method: "POST", path: path, body: body)
        return try await perform(req)
    }

    private func patch<T: Decodable>(_ path: String, body: some Encodable) async throws -> T {
        let req = try request(method: "PATCH", path: path, body: body)
        return try await perform(req)
    }
}

struct EmptyResponse: Decodable {}
struct SyncRequest: Encodable { let mode: String }
