package com.meli.orderbook.entity.wallet.model

import com.meli.orderbook.entity.wallet.exception.WalletOperationException
import java.math.BigDecimal

class Wallet(
    val id: Long,
    amountOfMoney: BigDecimal,
    amountOfVibranium: Int
) {

    var amountOfMoney: BigDecimal = amountOfMoney
        private set
    var amountOfVibranium: Int = amountOfVibranium
        private set

    fun subtractVibranium(size: Int) {
        if (size > amountOfVibranium || size <= 0) {
            throw WalletOperationException("Cant subtract $size from $amountOfVibranium vibranium in wallet")
        }

        this.amountOfVibranium -= size
    }

    fun depositVibranium(size: Int) {
        if (size < 0) {
            throw WalletOperationException("Cant deposit $size vibranium in wallet")
        }

        this.amountOfVibranium += size
    }

    fun depositMoney(deposit: BigDecimal) {
        if (deposit < BigDecimal.ZERO) {
            throw WalletOperationException("Cant deposit $deposit in wallet")
        }
        this.amountOfMoney += deposit
    }

    fun subtractMoney(subtract: BigDecimal) {
        if (subtract < BigDecimal.ZERO || subtract > this.amountOfMoney) {
            throw WalletOperationException("Cant subtract $subtract (reais) from $amountOfMoney (reais) in wallet")
        }


        this.amountOfMoney -= subtract
    }

    override fun toString(): String {
        return "Wallet(id=$id, amountOfMoney=$amountOfMoney, amountOfVibranium=$amountOfVibranium)"
    }
}