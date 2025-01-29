package com.codeplays.orderbook.usecase.order

import com.codeplays.orderbook.entity.order.exception.InvalidOrderType
import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.gateway.OrderQueryGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.codeplays.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.codeplays.orderbook.entity.wallet.model.Wallet
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