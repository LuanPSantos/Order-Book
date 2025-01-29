package com.codeplays.orderbook.usecase.order

import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.gateway.OrderQueryGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.codeplays.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.codeplays.orderbook.entity.wallet.model.Wallet
import org.slf4j.LoggerFactory

abstract class CancelOrderUseCase(
    private val orderQueryGateway: OrderQueryGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(input: Input) {
        log.info("m=execute, orderId=${input.orderId}")

        val order = getOrder(input.orderId)
        val wallet = walletQueryGateway.findById(order.walletId)

        cancelOrder(order, wallet)

        orderCommandGateway.update(order)
        walletCommandGateway.update(wallet)
    }

    private fun getOrder(orderId: Long): Order {
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