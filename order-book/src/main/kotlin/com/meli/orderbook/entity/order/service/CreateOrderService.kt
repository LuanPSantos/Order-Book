package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CreateOrderService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: OrderCommandGateway,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createBuyOrder(buyOrder: BuyOrder): BuyOrder {
        val buyerWallet = walletQueryGateway.findById(buyOrder.walletId)

        log.info("m=create, buyerWallet=$buyerWallet")

        buyerWallet.subtractMoney(buyOrder.price.multiply(buyOrder.size.toBigDecimal()))

        log.info("m=create, buyerWallet=$buyerWallet")

        walletCommandGateway.update(buyerWallet)

        val order = orderCommandGateway.create(buyOrder)

        log.info("m=create, buyOrder=$order")

        return BuyOrder(
            order.price,
            order.size,
            order.walletId,
            order.creationDate,
            order.id,
            order.state
        )
    }

    @Transactional
    fun createSellOrder(sellOrder: SellOrder): SellOrder {
        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)

        sellerWallet.subtractAssets(sellOrder.size)

        walletCommandGateway.update(sellerWallet)

        val order = orderCommandGateway.create(sellOrder)

        return SellOrder(
            order.price,
            order.size,
            order.walletId,
            order.creationDate,
            order.id,
            order.state
        )
    }
}