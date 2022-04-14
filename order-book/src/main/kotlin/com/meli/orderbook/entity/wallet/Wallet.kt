package com.meli.orderbook.entity.wallet

import java.math.BigDecimal

class Wallet(
    val id: Long,
    private var amountOfMoney: BigDecimal,
    private var amountOfAssets: Int
) {
    fun subtractAssets(size: Int) {
        if(size > amountOfAssets) {
            throw IllegalArgumentException("Insuficient amount of asset")
        }

        this.amountOfAssets -= size
    }

    fun getTheAmountOfMoney(): BigDecimal {
        return this.amountOfMoney
    }

    fun getTheAmountOfAssets(): Int {
        return this.amountOfAssets
    }
}