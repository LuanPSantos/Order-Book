package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.exception.InvalidOrderType
import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type.SALE
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CancelSaleOrderUseCase(
    orderQueryGateway: OrderQueryGateway,
    orderCommandGateway: OrderCommandGateway,
    walletQueryGateway: WalletQueryGateway,
    walletCommandGateway: WalletCommandGateway
) : CancelOrderUseCase(orderQueryGateway, orderCommandGateway, walletQueryGateway, walletCommandGateway) {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun validateOrder(order: Order) {
        log.info("m=validateOrder, orderType=${order.type}")

        if (order.type != SALE) {
            throw InvalidOrderType("Not a SALE order")
        }
    }

    override fun cancelOrder(order: Order, wallet: Wallet) {
        log.info("m=cancelOrder, orderId=${order.id}, walletId=${wallet.id}")

        val sizes = order.subtractAllSize()

        order.cancel()

        log.info("m=cancelOrder, returnVibranium=${sizes}")

        wallet.depositVibranium(sizes)
    }
}