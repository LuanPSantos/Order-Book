package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
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
                    BUY -> execute(matchedOrder, createdOrder, createdOrder.type)
                    SELL -> execute(createdOrder, matchedOrder, createdOrder.type)
                }
            }
        }
    }

    private fun execute(sellOrder: Order, buyOrder: Order, transactionType: Type) {
        log.info("m=execute, transactionType=$transactionType")

        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)
        val buyerWallet = walletQueryGateway.findById(buyOrder.walletId)

        log.info("m=execute, sellerWallet=${sellerWallet}, buyerWallet=${buyerWallet}")

        val transactionedMoney = exchangeMoney(sellerWallet, buyerWallet, sellOrder, buyOrder)
        val transactionedAssets = exchangeAsssets(buyerWallet, sellOrder, buyOrder)

        orderCommandGateway.update(sellOrder)
        orderCommandGateway.update(buyOrder)

        log.info("m=execute, sellOrder=$sellOrder, buyOrder=$buyOrder")

        walletCommandGateway.update(sellerWallet)
        walletCommandGateway.update(buyerWallet)

        log.info("m=execute, sellerWallet=$sellerWallet, buyerWallet=$buyerWallet")

        tradeHistoryCommandGateway.register(
            Trade(
                sellOrder.id!!,
                buyOrder.id!!,
                transactionType,
                transactionedAssets,
                transactionedMoney.totalExchanged,
                transactionedMoney.change
            )
        )
    }

    private fun exchangeMoney(
        sellerWallet: Wallet,
        buyerWallet: Wallet,
        sellOrder: Order,
        buyOrder: Order
    ): ExchangeMoneyResult {

        return if (thereHasMoreToSellThanToBuy(sellOrder, buyOrder)) {
            log.info("m=exchangeMoney, thereHasMoreToSellThanToBuy=true")

            val amountOfBuyingAssets = buyOrder.size

            exchangeMoneyWithChange(
                amountOfBuyingAssets,
                sellerWallet,
                buyerWallet,
                sellOrder,
                buyOrder
            )
        } else {
            log.info("m=exchangeMoney, thereHasMoreToSellThanToBuy=false")

            val amountOfSellingAssets = sellOrder.size

            exchangeMoneyWithChange(
                amountOfSellingAssets,
                sellerWallet,
                buyerWallet,
                sellOrder,
                buyOrder
            )
        }
    }

    private fun exchangeAsssets(buyerWallet: Wallet, sellOrder: Order, buyOrder: Order): Int {
        return if (thereHasMoreToSellThanToBuy(sellOrder, buyOrder)) {
            log.info("m=exchangeMoneyWithChange, thereHasMoreToSellThanToBuy=true")

            val amountOfBuyingAssets = buyOrder.subtractAllSize()
            sellOrder.subractSizes(amountOfBuyingAssets)

            buyerWallet.depositAssets(amountOfBuyingAssets)

            log.info("m=exchangeMoneyWithChange, amountOfBuyingAssets=$amountOfBuyingAssets")

            amountOfBuyingAssets
        } else if (thereHasLessToSellThanToBuy(sellOrder, buyOrder)) {
            log.info("m=exchangeMoneyWithChange, thereHasMoreToSellThanToBuy=false")

            val amountOfSellingAssets = sellOrder.subtractAllSize()
            buyOrder.subractSizes(amountOfSellingAssets)

            buyerWallet.depositAssets(amountOfSellingAssets)

            log.info("m=exchangeMoneyWithChange, amountOfSellingAssets=$amountOfSellingAssets")

            amountOfSellingAssets
        } else {
            log.info("m=exchangeMoneyWithChange, thereHasMoreToSellThanToBuy=equal")

            val assets = sellOrder.subtractAllSize()
            buyOrder.subtractAllSize()

            buyerWallet.depositAssets(assets)

            log.info("m=exchangeMoneyWithChange, assets=$assets")

            assets
        }
    }

    private fun exchangeMoneyWithChange(
        amountOfBuyingAssets: Int,
        sellerWallet: Wallet,
        buyerWallet: Wallet,
        sellOrder: Order,
        buyOrder: Order
    ): ExchangeMoneyResult {
        log.info("m=exchangeMoneyWithChange, amountOfBuyingAssets=$amountOfBuyingAssets")
        val result = ExchangeMoneyResult()

        val totalInTransaction = sellOrder.price.multiply(amountOfBuyingAssets.toBigDecimal())

        result.totalExchanged = sellOrder.price

        sellerWallet.depositMoney(totalInTransaction)

        if (buyOrder.price > sellOrder.price) {
            val change = buyOrder.price - sellOrder.price
            val totalInChange = change.multiply(amountOfBuyingAssets.toBigDecimal())
            result.change = change

            buyerWallet.depositMoney(totalInChange)
        }

        log.info("m=exchangeMoneyWithChange, totalInTransaction=${result.totalExchanged}, totalInChange=${result.change}")

        return result
    }

    private fun thereHasMoreToSellThanToBuy(sellOrder: Order, buyOrder: Order): Boolean {
        return (sellOrder.size - buyOrder.size) > 0
    }

    private fun thereHasLessToSellThanToBuy(sellOrder: Order, buyOrder: Order): Boolean {
        return (sellOrder.size - buyOrder.size) < 0
    }

    data class ExchangeMoneyResult(
        var totalExchanged: BigDecimal = BigDecimal.ZERO,
        var change: BigDecimal = BigDecimal.ZERO
    )
}