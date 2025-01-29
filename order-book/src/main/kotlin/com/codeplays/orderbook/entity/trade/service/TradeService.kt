package com.codeplays.orderbook.entity.trade.service

import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.Type
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.codeplays.orderbook.entity.trade.model.Trade
import com.codeplays.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.codeplays.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.codeplays.orderbook.entity.wallet.model.Wallet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TradeService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val tradeHistoryCommandGateway: TradeHistoryCommandGateway
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(createdOrder: Order, matchedOrders: List<Order>) {
        log.info("m=execute, createdOrder=$createdOrder, matchedOrders=${matchedOrders.size}")

        val matchedOrdersIterator = matchedOrders.iterator()
        while (matchedOrdersIterator.hasNext()) {
            val matchedOrder = matchedOrdersIterator.next()

            log.info("m=execute, matchedOrder=$matchedOrder")
            if (createdOrder.canTradeWith(matchedOrder)) {
                when (createdOrder.type) {
                    PURCHASE -> execute(matchedOrder, createdOrder, createdOrder.type)
                    SALE -> execute(createdOrder, matchedOrder, createdOrder.type)
                }
            }
        }
    }

    private fun execute(saleOrder: Order, purchaseOrder: Order, transactionType: Type) {
        log.info("m=execute, transactionType=$transactionType")

        val sellerWallet = walletQueryGateway.findById(saleOrder.walletId)
        val buyerWallet = walletQueryGateway.findById(purchaseOrder.walletId)

        val transactionedMoney = exchangeMoney(sellerWallet, buyerWallet, saleOrder, purchaseOrder)
        val transactionedSize = exchangeAsssets(buyerWallet, saleOrder, purchaseOrder)

        orderCommandGateway.update(saleOrder)
        orderCommandGateway.update(purchaseOrder)

        walletCommandGateway.update(sellerWallet)
        walletCommandGateway.update(buyerWallet)

        tradeHistoryCommandGateway.register(
            Trade(
                saleOrderId = saleOrder.id!!,
                purchaseOrderId = purchaseOrder.id!!,
                saleWalletId = sellerWallet.id,
                purchaseWalletId = buyerWallet.id,
                type = transactionType,
                size = transactionedSize,
                price = transactionedMoney.price,
                change = transactionedMoney.change
            )
        )
    }

    private fun exchangeMoney(
        saleWallet: Wallet,
        purchaseWallet: Wallet,
        saleOrder: Order,
        purchaseOrder: Order
    ): ExchangeMoneyResult {

        return if (thereIsMoreForSaleThanForPurchase(saleOrder, purchaseOrder)) {
            log.info("m=exchangeMoney, thereIsMoreForSaleThanForPurchase=true")

            exchangeMoneyWithChange(
                purchaseOrder.size,
                saleWallet,
                purchaseWallet,
                saleOrder,
                purchaseOrder
            )
        } else {
            log.info("m=exchangeMoney, thereIsMoreForSaleThanForPurchase=false")

            exchangeMoneyWithChange(
                saleOrder.size,
                saleWallet,
                purchaseWallet,
                saleOrder,
                purchaseOrder
            )
        }
    }

    private fun exchangeAsssets(buyerWallet: Wallet, saleOrder: Order, purchaseOrder: Order): Int {
        return if (thereIsMoreForSaleThanForPurchase(saleOrder, purchaseOrder)) {
            log.info("m=exchangeAsssets, thereIsMoreForSaleThanForPurchase=true")

            val purchaseSize = purchaseOrder.subtractAllSize()
            saleOrder.subractSize(purchaseSize)

            buyerWallet.depositVibranium(purchaseSize)

            log.info("m=exchangeAsssets, purchaseSize=$purchaseSize")

            purchaseSize
        } else {
            log.info("m=exchangeAsssets, thereIsMoreForSaleThanForPurchase=false")

            val saleSize = saleOrder.subtractAllSize()
            purchaseOrder.subractSize(saleSize)

            buyerWallet.depositVibranium(saleSize)

            log.info("m=exchangeAsssets, saleSize=$saleSize")

            saleSize
        }
    }

    private fun exchangeMoneyWithChange(
        size: Int,
        sellerWallet: Wallet,
        buyerWallet: Wallet,
        saleOrder: Order,
        purchaseOrder: Order
    ): ExchangeMoneyResult {
        log.info("m=exchangeMoneyWithChange, size=$size")
        val result = ExchangeMoneyResult()
        result.price = saleOrder.price

        val totalInTransaction = saleOrder.price.multiply(size.toBigDecimal())

        sellerWallet.depositMoney(totalInTransaction)

        if (purchaseOrder.price > saleOrder.price) {
            val change = purchaseOrder.price - saleOrder.price
            result.change = change

            val totalInChange = change.multiply(size.toBigDecimal())

            buyerWallet.depositMoney(totalInChange)
        }

        log.info("m=exchangeMoneyWithChange, totalInTransaction=${result.price}, totalInChange=${result.change}")

        return result
    }

    private fun thereIsMoreForSaleThanForPurchase(saleOrder: Order, purchaseOrder: Order): Boolean {
        return (saleOrder.size - purchaseOrder.size) > 0
    }

    data class ExchangeMoneyResult(
        var price: BigDecimal = BigDecimal.ZERO,
        var change: BigDecimal = BigDecimal.ZERO
    )
}