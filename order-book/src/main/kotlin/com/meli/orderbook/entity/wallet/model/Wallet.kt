package com.meli.orderbook.entity.wallet.model

import java.math.BigDecimal

class Wallet(
    val id: Long,
    private var amountOfMoney: BigDecimal,
    private var amountOfAssets: Int
) {
    fun subtractAssets(size: Int) {
        if (size > amountOfAssets) {
            throw IllegalArgumentException("Insuficient amount of asset")
        }

        this.amountOfAssets -= size
    }

    fun depositMoney(amountOfMoney: BigDecimal) {
        this.amountOfMoney.plus(amountOfMoney)
    }

    fun getTheAmountOfMoney(): BigDecimal {
        return this.amountOfMoney
    }

    fun getTheAmountOfAssets(): Int {
        return this.amountOfAssets
    }

    fun depositAssets(size: Int) {
        if(size < 0) {
            throw IllegalArgumentException("Depositing negative size")
        }

        this.amountOfAssets += size
    }
}