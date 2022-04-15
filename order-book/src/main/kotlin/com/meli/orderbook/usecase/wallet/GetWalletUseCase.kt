package com.meli.orderbook.usecase.wallet

import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import org.springframework.stereotype.Service

@Service
class GetWalletUseCase(
    private val walletQueryGateway: WalletQueryGateway
) {

    fun execute(input: Input): Output {

        val wallet = walletQueryGateway.findById(input.walletId)

        return Output(wallet)
    }

    data class Input(
        val walletId: Long
    )

    data class Output(
        val wallet: Wallet
    )
}