package com.meli.orderbook.entity.wallet.gateway

import com.meli.orderbook.entity.wallet.model.Wallet

interface WalletQueryGateway {

    fun findById(id: Long): Wallet
}