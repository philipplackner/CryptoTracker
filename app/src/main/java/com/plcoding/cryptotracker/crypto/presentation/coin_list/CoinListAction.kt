package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi

interface CoinListAction {
    fun selectCoin(coinUi: CoinUi)
}