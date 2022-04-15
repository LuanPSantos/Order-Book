package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway

class CancelOrderUseCase(
    private val orderQueryGateway: OrderQueryGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway
) {

    fun execute(input: Input) {

        val order = orderQueryGateway.findById(input.orderId)
        val wallet = walletQueryGateway.findById(order.walletId)

        val sizes = order.subtractAllSize()
        order.cancel()

        wallet.depositAssets(sizes)

        orderCommandGateway.update(order)
        walletCommandGateway.update(wallet)
    }

    data class Input(
        val orderId: Long
    )
}