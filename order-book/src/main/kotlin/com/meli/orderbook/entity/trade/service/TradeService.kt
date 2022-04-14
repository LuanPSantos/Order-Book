package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.transaction.gateway.TransactionHistoricCommandGateway
import com.meli.orderbook.entity.transaction.model.Transaction
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import java.math.BigDecimal
import javax.transaction.Transactional

class TradeService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val trasactionHistoricCommandGateway: TransactionHistoricCommandGateway
) {

    fun executeSell(sellOrder: SellOrder, buyOrder: BuyOrder) {
        execute(sellOrder, buyOrder, SELL)
    }

    fun executeBuy(buyOrder: BuyOrder, sellOrder: SellOrder) {
        execute(sellOrder, buyOrder, BUY)
    }

    @Transactional
    private fun execute(sellOrder: SellOrder, buyOrder: BuyOrder, transactionType: Order.Type) {
        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)
        val buyerWallet = walletQueryGateway.findById(buyOrder.walletId)

        val transactionedMoney = depositMoneyToSeller(sellerWallet, sellOrder, buyOrder)
        val transactionedAssets = depositAssetToBuyer(buyerWallet, sellOrder, buyOrder)

        orderCommandGateway.update(sellOrder)
        orderCommandGateway.update(buyOrder)

        walletCommandGateway.update(sellerWallet)
        walletCommandGateway.update(buyerWallet)

        trasactionHistoricCommandGateway.register(
            Transaction(
                sellerWallet.id,
                buyerWallet.id,
                transactionType,
                transactionedAssets,
                transactionedMoney
            )
        )
    }

    private fun depositMoneyToSeller(
        sellerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ): BigDecimal {
        return if (thereIsMoreToSellThanToBuy(sellOrder, buyOrder)) {
            val amountOfBuyingAssets = buyOrder.getAllSizesAndCloseOrder().toBigDecimal()
            sellerWallet.depositMoney(buyOrder.price.multiply(amountOfBuyingAssets))

            amountOfBuyingAssets
        } else {
            val amountOfSellingAssets = sellOrder.getAllSizesAndCloseOrder().toBigDecimal()
            sellerWallet.depositMoney(buyOrder.price.multiply(amountOfSellingAssets))

            amountOfSellingAssets
        }
    }

    private fun depositAssetToBuyer(
        buyerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ): Int {
        return if (thereIsMoreToSellThanToBuy(sellOrder, buyOrder)) {
            val amountOfBuyingAssets = buyOrder.getAllSizesAndCloseOrder()
            buyerWallet.depositAssets(amountOfBuyingAssets)

            amountOfBuyingAssets
        } else {
            val amountOfSellingAssets = sellOrder.getAllSizesAndCloseOrder()
            buyerWallet.depositAssets(amountOfSellingAssets)

            amountOfSellingAssets
        }
    }

    private fun thereIsMoreToSellThanToBuy(sellOrder: SellOrder, buyOrder: BuyOrder): Boolean {
        return sellOrder.size - buyOrder.size > 0
    }
}