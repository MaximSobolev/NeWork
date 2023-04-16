package ru.netology.nework.error

import ru.netology.nework.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManipulationError @Inject constructor() {
    fun manipulation(e : AppError) : Int {
        when (e) {
            is AppError.ApiError -> {
                if (e.status == 400) {
                    return R.string.login_or_password_are_wrong
                }
                return R.string.retry_text
            }
            is AppError.NetworkError -> {
                return R.string.network_problems
            }
            is AppError.UnknownError -> {
                return R.string.unknown_problems
            }
        }
    }
}