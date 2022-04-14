package com.meli.orderbook.entity.wallet.gateway

import com.meli.orderbook.entity.wallet.Wallet

interface WalletCommandGateway {

    fun update(wallet: Wallet)
}