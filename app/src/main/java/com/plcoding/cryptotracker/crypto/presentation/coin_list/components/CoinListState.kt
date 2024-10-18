package com.plcoding.cryptotracker.crypto.presentation.coin_list.components

import androidx.compose.runtime.Immutable
import com.plcoding.cryptotracker.crypto.presentation.CoinUi

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUi> = emptyList(),
    val selectedCoin: CoinUi? = null,
)