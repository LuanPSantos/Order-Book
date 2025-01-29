package com.codeplays.orderbook.entity.wallet.gateway

import com.codeplays.orderbook.entity.wallet.model.Wallet

interface WalletCommandGateway {

    fun update(wallet: Wallet)
}