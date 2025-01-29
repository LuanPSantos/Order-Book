package com.codeplays.orderbook.entity.wallet.model

import com.codeplays.orderbook.entity.wallet.exception.WalletOperationException
import org.slf4j.LoggerFactory
import java.math.BigDecimal

class Wallet(
    val id: Long,
    amountOfMoney: BigDecimal,
    amountOfVibranium: Int
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    var amountOfMoney: BigDecimal = amountOfMoney
        private set
    var amountOfVibranium: Int = amountOfVibranium
        private set

    fun subtractVibranium(size: Int) {
        log.info("m=subtractVibranium, size=$size, walletId=$id")

        if (size > amountOfVibranium || size <= 0) {
            throw WalletOperationException("Cant subtract $size from $amountOfVibranium vibranium in wallet")
        }

        this.amountOfVibranium -= size
    }

    fun depositVibranium(size: Int) {
        log.info("m=depositVibranium, size=$size, walletId=$id")

        if (size < 0) {
            throw WalletOperationException("Cant deposit $size vibranium in wallet")
        }

        this.amountOfVibranium += size
    }

    fun depositMoney(deposit: BigDecimal) {
        log.info("m=depositMoney, deposit=$deposit, walletId=$id")

        if (deposit < BigDecimal.ZERO) {
            throw WalletOperationException("Cant deposit $deposit in wallet")
        }
        this.amountOfMoney += deposit
    }

    fun subtractMoney(subtract: BigDecimal) {
        log.info("m=subtractMoney, subtract=$subtract, walletId=$id")

        if (subtract < BigDecimal.ZERO || subtract > this.amountOfMoney) {
            throw WalletOperationException("Cant subtract $subtract (reais) from $amountOfMoney (reais) in wallet")
        }


        this.amountOfMoney -= subtract
    }

    override fun toString(): String {
        return "Wallet(id=$id, amountOfMoney=$amountOfMoney, amountOfVibranium=$amountOfVibranium)"
    }
}