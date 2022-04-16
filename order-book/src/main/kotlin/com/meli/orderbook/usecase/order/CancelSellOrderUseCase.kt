package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import org.springframework.stereotype.Service

@Service
class CancelSellOrderUseCase(
    orderQueryGateway: OrderQueryGateway,
    orderCommandGateway: OrderCommandGateway,
    walletQueryGateway: WalletQueryGateway,
    walletCommandGateway: WalletCommandGateway
) : CancelOrderUseCase(orderQueryGateway, orderCommandGateway, walletQueryGateway, walletCommandGateway) {

    override fun validateOrder(order: Order) {
        if (order.type != Order.Type.SELL) {
            throw IllegalStateException("Not a sell order")
        }
    }

    override fun cancelOrder(order: Order, wallet: Wallet) {
        val sizes = order.subtractAllSize()

        order.cancel()

        wallet.depositAssets(sizes)
    }
}