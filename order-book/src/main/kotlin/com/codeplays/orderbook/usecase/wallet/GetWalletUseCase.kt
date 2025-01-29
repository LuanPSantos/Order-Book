package com.codeplays.orderbook.usecase.wallet

import com.codeplays.orderbook.entity.order.gateway.OrderQueryGateway
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.codeplays.orderbook.entity.wallet.model.Wallet
import com.codeplays.orderbook.usecase.wallet.GetWalletUseCase.Output.InTrade
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GetWalletUseCase(
    private val walletQueryGateway: WalletQueryGateway,
    private val orderQueryGateway: OrderQueryGateway
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(input: Input): Output {
        log.info("m=execute, walletId=${input.walletId}")

        val wallet = walletQueryGateway.findById(input.walletId)

        val ordersInTrade = orderQueryGateway.findAllOrdersInTradeByWalletId(wallet.id)

        var totalMoneyInTrade = BigDecimal.ZERO
        ordersInTrade
            .filter { it.type == PURCHASE }
            .map { it.price.multiply(it.size.toBigDecimal()) }
            .forEach { totalMoneyInTrade += it }

        var totalSizeInTrade = 0
        ordersInTrade
            .filter { it.type == SALE }
            .map { it.size }
            .forEach { totalSizeInTrade += it }

        log.info("m=execute, totalMoneyInTrade=$totalMoneyInTrade, totalSizeInTrade=$totalSizeInTrade")

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