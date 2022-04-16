package com.meli.orderbook.entity.order.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import org.springframework.stereotype.Service

@Service
class CreateSellOrderService(
    walletQueryGateway: WalletQueryGateway,
    walletCommandGateway: WalletCommandGateway,
    orderCommandGateway: OrderCommandGateway,
) : CreateOrderService(walletQueryGateway, walletCommandGateway, orderCommandGateway) {

    override fun subtractValueFromWallet(wallet: Wallet, order: Order) {
        wallet.subtractVibranium(order.size)
    }
}