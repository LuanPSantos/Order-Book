package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CreatePurchaseOrderService(
    walletQueryGateway: WalletQueryGateway,
    walletCommandGateway: WalletCommandGateway,
    orderCommandGateway: OrderCommandGateway,
) : CreateOrderService(walletQueryGateway, walletCommandGateway, orderCommandGateway) {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun subtractValueFromWallet(wallet: Wallet, order: Order) {
        val value = order.price.multiply(order.size.toBigDecimal())

        log.info("m=subtractValueFromWallet, value=$value")

        wallet.subtractMoney(value)
    }
}