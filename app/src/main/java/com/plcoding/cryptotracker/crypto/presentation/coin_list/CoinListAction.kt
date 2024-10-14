package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinClick(val coinUi: CoinUi): CoinListAction
}