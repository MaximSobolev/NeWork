package ru.netology.nework.error

sealed class AppError(var code : String) : java.lang.RuntimeException() {
    class ApiError(val status : Int, code: String) : AppError(code)
    class NetworkError : AppError("error_network")
    class UnknownError : AppError("error_unknown")
}