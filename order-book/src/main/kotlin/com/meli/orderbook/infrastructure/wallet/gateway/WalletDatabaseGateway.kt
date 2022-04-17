package com.meli.orderbook.infrastructure.wallet.gateway

import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import com.meli.orderbook.infrastructure.config.db.schema.WalletSchema
import com.meli.orderbook.infrastructure.config.db.repository.WalletRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class WalletDatabaseGateway(
    private val walletRepository: WalletRepository
) : WalletQueryGateway, WalletCommandGateway {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun update(wallet: Wallet) {
        log.info("m=update, wallet=$wallet")

        walletRepository.save(WalletSchema(wallet.id, wallet.amountOfMoney, wallet.amountOfVibranium))
    }

    override fun findById(id: Long): Wallet {
        log.info("m=findById, walletId=$id")

        val schema = walletRepository.findWalletById(id)

        return Wallet(schema.id, schema.amountOfMoney, schema.amountOfVibranium)
    }
}