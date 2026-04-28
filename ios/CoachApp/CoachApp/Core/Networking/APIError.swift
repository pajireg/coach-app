import Foundation

enum APIError: Error, LocalizedError {
    case invalidURL
    case unauthorized
    case notFound
    case serverError(Int)
    case decodingFailed(Error)
    case networkFailed(Error)

    var errorDescription: String? {
        switch self {
        case .invalidURL: "잘못된 서버 주소입니다."
        case .unauthorized: "API 키가 유효하지 않습니다."
        case .notFound: "요청한 데이터를 찾을 수 없습니다."
        case .serverError(let code): "서버 오류가 발생했습니다. (\(code))"
        case .decodingFailed: "데이터를 처리할 수 없습니다."
        case .networkFailed: "네트워크 연결을 확인해주세요."
        }
    }
}
