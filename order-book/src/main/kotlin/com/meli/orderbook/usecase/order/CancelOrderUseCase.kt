package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet

abstract class CancelOrderUseCase(
    private val orderQueryGateway: OrderQueryGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway
) {

    fun execute(input: Input) {

        val order = getSellOrder(input.orderId)
        val wallet = walletQueryGateway.findById(order.walletId)

        cancelOrder(order, wallet)

        orderCommandGateway.update(order)
        walletCommandGateway.update(wallet)
    }

    private fun getSellOrder(orderId: Long): Order {
        val order = orderQueryGateway.findById(orderId)

        validateOrder(order)

        return order
    }

    protected abstract fun cancelOrder(order: Order, wallet: Wallet)

    protected abstract fun validateOrder(order: Order)

    data class Input(
        val orderId: Long
    )
}