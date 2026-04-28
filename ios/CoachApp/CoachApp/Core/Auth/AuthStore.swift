import Foundation
import Observation

@Observable
final class AuthStore {
    private(set) var apiKey: String?
    private(set) var baseURL: String

    var isAuthenticated: Bool { apiKey != nil }

    init() {
        self.apiKey = Keychain.load(forKey: Keychain.apiKeyAccount)
        self.baseURL = Keychain.load(forKey: Keychain.baseURLAccount) ?? "http://localhost:8000"
    }

    func save(apiKey: String, baseURL: String) throws {
        try Keychain.save(apiKey, forKey: Keychain.apiKeyAccount)
        try Keychain.save(baseURL, forKey: Keychain.baseURLAccount)
        self.apiKey = apiKey
        self.baseURL = baseURL
    }

    func signOut() {
        Keychain.delete(forKey: Keychain.apiKeyAccount)
        apiKey = nil
    }
}
