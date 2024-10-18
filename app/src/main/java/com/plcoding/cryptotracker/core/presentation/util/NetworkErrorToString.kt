package com.plcoding.cryptotracker.core.presentation.util

import android.content.Context
import com.plcoding.cryptotracker.R
import com.plcoding.cryptotracker.core.domain.util.NetworkError

fun NetworkError.toString(context: Context): String {
    val resId = when (this) {
        NetworkError.REQUEST_TIMEOUT -> R.string.the_request_timed_out
        NetworkError.TOO_MANY_REQUESTS -> R.string.too_many_requests
        NetworkError.NO_INTERNET_CONNECTION -> R.string.no_internet_connection
        NetworkError.SERVER_ERROR -> R.string.server_error
        NetworkError.SERIALIZATION -> R.string.serialization_error
        NetworkError.UNKNOWN -> R.string.unknown_error
    }
    return context.getString(resId)
}