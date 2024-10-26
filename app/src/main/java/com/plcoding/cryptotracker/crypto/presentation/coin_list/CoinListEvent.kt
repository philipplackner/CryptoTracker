package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi

sealed interface CoinListEvent {
    data class Error(val error: NetworkError) : CoinListEvent
    data class NavigateToDetails(val coinUi: CoinUi) : CoinListEvent
}