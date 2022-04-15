package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import java.math.BigDecimal

class TradeService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val tradeHistoryCommandGateway: TradeHistoryCommandGateway
) {

    fun executeSell(sellOrder: SellOrder, matchingBuyOrders: List<BuyOrder>) {
        val buyOrders = matchingBuyOrders.iterator()
        while (buyOrders.hasNext()) {
            val buyOrder = buyOrders.next()
            if (sellOrder.canTradeWith(buyOrder)) {
                execute(sellOrder, buyOrder, SELL)
            }
        }
    }

    fun executeBuy(buyOrder: BuyOrder, matchingSellOrders: List<SellOrder>) {
        val sellOrders = matchingSellOrders.iterator()
        while (sellOrders.hasNext()) {
            val sellOrder = sellOrders.next()
            if (sellOrder.canTradeWith(buyOrder)) {
                execute(sellOrder, buyOrder, BUY)
            }
        }
    }

    private fun execute(sellOrder: SellOrder, buyOrder: BuyOrder, transactionType: Order.Type) {

        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)
        val buyerWallet = walletQueryGateway.findById(buyOrder.walletId)

        val transactionedMoney = exchangeMoney(sellerWallet, buyerWallet, sellOrder, buyOrder)
        val transactionedAssets = exchangeAsssets(buyerWallet, sellOrder, buyOrder)

        orderCommandGateway.update(sellOrder)
        orderCommandGateway.update(buyOrder)

        walletCommandGateway.update(sellerWallet)
        walletCommandGateway.update(buyerWallet)

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
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ): ExchangeMoneyResult {

        return if (thereHasMoreToSellThanToBuy(sellOrder, buyOrder)) {

            val amountOfBuyingAssets = buyOrder.size

            exchangeMoneyWithChange(
                amountOfBuyingAssets,
                sellerWallet,
                buyerWallet,
                sellOrder,
                buyOrder
            )
        } else {

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

    private fun exchangeAsssets(buyerWallet: Wallet, sellOrder: SellOrder, buyOrder: BuyOrder): Int {
        return if (thereHasMoreToSellThanToBuy(sellOrder, buyOrder)) {

            val amountOfBuyingAssets = buyOrder.subtractAllSize()
            sellOrder.subractSizes(amountOfBuyingAssets)

            buyerWallet.depositAssets(amountOfBuyingAssets)

            amountOfBuyingAssets
        } else if (thereHasLessToSellThanToBuy(sellOrder, buyOrder)) {

            val amountOfSellingAssets = sellOrder.subtractAllSize()
            buyOrder.subractSizes(amountOfSellingAssets)

            buyerWallet.depositAssets(amountOfSellingAssets)

            amountOfSellingAssets
        } else {

            val assets = sellOrder.subtractAllSize()
            buyOrder.subtractAllSize()

            buyerWallet.depositAssets(assets)

            assets
        }
    }

    private fun exchangeMoneyWithChange(
        amountOfBuyingAssets: Int,
        sellerWallet: Wallet,
        buyerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ): ExchangeMoneyResult {
        val result = ExchangeMoneyResult()

        val totalInTransaction = sellOrder.price.multiply(amountOfBuyingAssets.toBigDecimal())

        result.totalExchanged = totalInTransaction

        sellerWallet.depositMoney(totalInTransaction)

        if (buyOrder.price > sellOrder.price) {
            val change = buyOrder.price - sellOrder.price
            val totalInChange = change.multiply(amountOfBuyingAssets.toBigDecimal())
            result.change = totalInChange

            buyerWallet.depositMoney(totalInChange)
        }

        return result
    }

    private fun thereHasMoreToSellThanToBuy(sellOrder: SellOrder, buyOrder: BuyOrder): Boolean {
        return (sellOrder.size - buyOrder.size) > 0
    }

    private fun thereHasLessToSellThanToBuy(sellOrder: SellOrder, buyOrder: BuyOrder): Boolean {
        return (sellOrder.size - buyOrder.size) < 0
    }

    data class ExchangeMoneyResult(
        var totalExchanged: BigDecimal = BigDecimal.ZERO,
        var change: BigDecimal = BigDecimal.ZERO
    )
}