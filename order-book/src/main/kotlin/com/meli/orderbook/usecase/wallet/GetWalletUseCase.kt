package com.meli.orderbook.usecase.wallet

import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import com.meli.orderbook.usecase.wallet.GetWalletUseCase.Output.InTrade
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GetWalletUseCase(
    private val walletQueryGateway: WalletQueryGateway,
    private val orderQueryGateway: OrderQueryGateway
) {

    fun execute(input: Input): Output {

        val wallet = walletQueryGateway.findById(input.walletId)

        val ordersInTrade = orderQueryGateway.findAllOrdersInTradeByWallet(wallet.id)

        var totalMoneyInTrade = BigDecimal.ZERO
        ordersInTrade
            .filter { it.type == BUY }
            .map { it.price.multiply(it.size.toBigDecimal()) }
            .forEach { totalMoneyInTrade += it }

        var totalSizeInTrade = 0
        ordersInTrade
            .filter { it.type == SELL }
            .map { it.size }
            .forEach { totalSizeInTrade += it }

        return Output(wallet, InTrade(totalMoneyInTrade, totalSizeInTrade))
    }

    data class Input(
        val walletId: Long
    )

    data class Output(
        val wallet: Wallet,
        val inTrade: InTrade
    ) {
        data class InTrade(
            val amountOfMoney: BigDecimal,
            val amountOfVibranium: Int
        )
    }
}