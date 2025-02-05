package com.codeplays.orderbook.infrastructure.wallet.controller

import com.codeplays.orderbook.entity.wallet.model.Wallet
import com.codeplays.orderbook.usecase.wallet.GetWalletUseCase
import com.codeplays.orderbook.usecase.wallet.GetWalletUseCase.Input
import com.codeplays.orderbook.usecase.wallet.GetWalletUseCase.Output.InTrade
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetWalletController(
    private val getWalletUseCase: GetWalletUseCase
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("api/v1/wallets/{id}")
    fun getWalletById(@PathVariable id: Long): Response {
        log.info("m=getWalletById, walletId=$id")

        val output = getWalletUseCase.execute(Input(id))

        return Response(
            output.wallet,
            output.inTrade
        )
    }

    data class Response(
        val wallet: Wallet,
        val inTrade: InTrade
    )
}