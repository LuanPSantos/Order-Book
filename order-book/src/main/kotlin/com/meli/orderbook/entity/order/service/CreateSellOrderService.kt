package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import javax.transaction.Transactional

class CreateSellOrderService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: OrderCommandGateway,
) {

    @Transactional
    @Suppress("UNCHECKED_CAST")
    fun <T : Order> create(sellOrder: T): T {
        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)

        sellerWallet.subtractAssets(sellOrder.size)

        walletCommandGateway.update(sellerWallet)

        return orderCommandGateway.create(sellOrder) as T
    }
}