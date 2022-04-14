package com.meli.orderbook.entity.wallet.gateway

import com.meli.orderbook.entity.wallet.Wallet

interface WalletQueryGateway {

    fun findById(id: Long): Wallet
}