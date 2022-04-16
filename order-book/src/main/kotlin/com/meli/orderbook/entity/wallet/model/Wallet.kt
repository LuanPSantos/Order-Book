package com.meli.orderbook.entity.wallet.model

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
            throw IllegalArgumentException("Invalid size to subtract")
        }

        this.amountOfVibranium -= size
    }

    fun depositVibranium(size: Int) {
        if (size < 0) {
            throw IllegalArgumentException("Invalid size to deposit")
        }

        this.amountOfVibranium += size
    }

    fun depositMoney(deposit: BigDecimal) {
        if (deposit < BigDecimal.ZERO) {
            throw IllegalArgumentException("Invalid value to deposit")
        }
        this.amountOfMoney += deposit
    }

    fun subtractMoney(subtract: BigDecimal) {
        if (subtract < BigDecimal.ZERO || subtract > this.amountOfMoney) {
            throw IllegalArgumentException("Invalid value to subtract")
        }


        this.amountOfMoney -= subtract
    }

    override fun toString(): String {
        return "Wallet(id=$id, amountOfMoney=$amountOfMoney, amountOfVibranium=$amountOfVibranium)"
    }
}