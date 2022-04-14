package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway

class TradeService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: WalletCommandGateway
) {

    fun execute(sellOrder: SellOrder, buyOrder: BuyOrder) {
        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)
        val buyerWallet = walletQueryGateway.findById(buyOrder.walletId)

        //sellOrder.addMonay(buyOrder.price.multiply(buyOrder.size.toBigDecimal()))
    }
}