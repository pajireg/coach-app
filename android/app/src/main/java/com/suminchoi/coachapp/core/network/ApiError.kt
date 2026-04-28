package com.suminchoi.coachapp.core.network

sealed class ApiError : Exception() {
    data object Unauthorized : ApiError() {
        override val message = "인증이 필요합니다. 다시 로그인해주세요."
    }
    data class Server(val status: Int, override val message: String?) : ApiError()
    data class Network(override val cause: Throwable?) : ApiError() {
        override val message = "네트워크 연결을 확인해주세요."
    }
    data class Decoding(override val cause: Throwable?) : ApiError() {
        override val message = "응답을 처리할 수 없습니다."
    }
}
