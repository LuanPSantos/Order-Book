package com.meli.orderbook.entity.wallet.model

import java.math.BigDecimal

class Wallet(
    val id: Long,
    amountOfMoney: BigDecimal,
    amountOfAssets: Int
) {

    var amountOfMoney: BigDecimal = amountOfMoney
        private set
    var amountOfAssets: Int = amountOfAssets
        private set

    fun subtractAssets(size: Int) {
        if (size > amountOfAssets || size <= 0) {
            throw IllegalArgumentException("Invalid size to subtract")
        }

        this.amountOfAssets -= size
    }

    fun depositAssets(size: Int) {
        if (size < 0) {
            throw IllegalArgumentException("Invalid size to deposit")
        }

        this.amountOfAssets += size
    }

    fun depositMoney(deposit: BigDecimal) {
        if (deposit < BigDecimal.ZERO) {
            throw IllegalArgumentException("Invalid deposit value")
        }
        this.amountOfMoney += deposit
    }
}