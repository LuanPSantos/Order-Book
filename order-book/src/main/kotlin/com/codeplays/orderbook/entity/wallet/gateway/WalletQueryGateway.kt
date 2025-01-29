package com.codeplays.orderbook.entity.wallet.gateway

import com.codeplays.orderbook.entity.wallet.model.Wallet

interface WalletQueryGateway {

    fun findById(id: Long): Wallet
}