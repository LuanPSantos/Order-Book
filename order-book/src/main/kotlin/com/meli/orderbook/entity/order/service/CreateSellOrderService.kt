package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
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
    fun create(sellOrder: SellOrder): SellOrder {
        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)

        sellerWallet.subtractAssets(sellOrder.size)

        walletCommandGateway.update(sellerWallet)

        return orderCommandGateway.create(sellOrder) as SellOrder
    }
}