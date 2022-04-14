package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet

class TradeService(
    private val walletQueryGateway: WalletQueryGateway,
    private val walletCommandGateway: WalletCommandGateway,
    private val orderCommandGateway: WalletCommandGateway
) {

    fun execute(sellOrder: SellOrder, buyOrder: BuyOrder) {
        val sellerWallet = walletQueryGateway.findById(sellOrder.walletId)
        val buyerWallet = walletQueryGateway.findById(buyOrder.walletId)

        depositMoneyToSeller(sellerWallet, sellOrder, buyOrder)
        depositAssetToBuyer(buyerWallet, sellOrder, buyOrder)

    }

    private fun depositMoneyToSeller(
        sellerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ) {
        if (thereIsMoreToSellThanToBuy(sellOrder, buyOrder)) {
            sellerWallet.depositMoney(buyOrder.price.multiply(buyOrder.size.toBigDecimal()))
        } else {
            sellerWallet.depositMoney(buyOrder.price.multiply(sellOrder.size.toBigDecimal()))
        }
    }

    private fun thereIsMoreToSellThanToBuy(sellOrder: SellOrder, buyOrder: BuyOrder): Boolean {
        return sellOrder.size - buyOrder.size > 0
    }

    private fun depositAssetToBuyer(
        buyerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder
    ) {
        if (thereIsMoreToSellThanToBuy(sellOrder, buyOrder)) {
            buyerWallet.depositAssets(buyOrder.size)
        } else {
            buyerWallet.depositAssets(sellOrder.size)
        }
    }
}