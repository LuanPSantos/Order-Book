package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

abstract class CreateOrderService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: OrderCommandGateway,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun createOrder(order: Order): Order {
        log.info("m=createOrder, order=$order")

        val wallet = walletQueryGateway.findById(order.walletId)

        subtractValueFromWallet(wallet, order)

        walletCommandGateway.update(wallet)

        return orderCommandGateway.create(order)
    }

    protected abstract fun subtractValueFromWallet(wallet: Wallet, order: Order)
}