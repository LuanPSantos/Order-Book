package com.meli.orderbook.infrastructure.wallet.controller

import com.meli.orderbook.usecase.wallet.GetWalletUseCase
import com.meli.orderbook.usecase.wallet.GetWalletUseCase.Input
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class GetWalletController(
    private val getWalletUseCase: GetWalletUseCase
) {

    @GetMapping("api/v1/wallets/{id}")
    fun getWalletById(@PathVariable id: Long): Response {
        val output = getWalletUseCase.execute(Input(id))

        return Response(
            output.wallet.id,
            output.wallet.amountOfMoney,
            output.wallet.amountOfVibranium,
            output.totalMoneyInTrade,
            output.totalSizeInTrade
        )
    }

    data class Response(
        val id: Long,
        val amountOfMoney: BigDecimal,
        val amountOfVibranium: Int,
        val amountOfMoneyInTrade: BigDecimal,
        val amountOfVibraniumInTrade: Int,
    )
}