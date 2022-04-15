package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.trade.gateway.TradeHistoricCommandGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import java.math.BigDecimal
import javax.transaction.Transactional

class TradeService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val trasactionHistoricCommandGateway: TradeHistoricCommandGateway
) {

    fun executeSell(sellOrder: SellOrder, buyOrder: BuyOrder) {
        execute(sellOrder, buyOrder, SELL)
    }

    fun executeBuy(buyOrder: BuyOrder, sellOrder: SellOrder) {
        execute(sellOrder, buyOrder, BUY)
    }

    @Transactional
    private fun execute(sellOrder: SellOrder, buyOrder: BuyOrder, transactionType: Order.Type) {

        if (sellOrder.price > buyOrder.price) return

        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)
        val buyerWallet = walletQueryGateway.findById(buyOrder.walletId)

        val transactionedMoney = exchangeMoney(sellerWallet, buyerWallet, sellOrder, buyOrder)
        val transactionedAssets = exchangeAsssets(buyerWallet, sellOrder, buyOrder)

        orderCommandGateway.update(sellOrder)
        orderCommandGateway.update(buyOrder)

        walletCommandGateway.update(sellerWallet)
        walletCommandGateway.update(buyerWallet)

        trasactionHistoricCommandGateway.register(
            Trade(
                sellerWallet.id,
                buyerWallet.id,
                transactionType,
                transactionedAssets,
                transactionedMoney
            )
        )
    }

    private fun exchangeMoney(
        sellerWallet: Wallet,
        buyerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ): BigDecimal {
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

    private fun exchangeAsssets(
        buyerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ): Int {
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
    ): BigDecimal {
        val totalInTransaction = sellOrder.price.multiply(amountOfBuyingAssets.toBigDecimal())

        sellerWallet.depositMoney(totalInTransaction)

        if (buyOrder.price > sellOrder.price) {
            val change = buyOrder.price - sellOrder.price
            buyerWallet.depositMoney(change.multiply(amountOfBuyingAssets.toBigDecimal()))
        }

        return totalInTransaction
    }

    private fun thereHasMoreToSellThanToBuy(sellOrder: SellOrder, buyOrder: BuyOrder): Boolean {
        return (sellOrder.size - buyOrder.size) > 0
    }

    private fun thereHasLessToSellThanToBuy(sellOrder: SellOrder, buyOrder: BuyOrder): Boolean {
        return (sellOrder.size - buyOrder.size) < 0
    }
}