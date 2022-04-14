package com.meli.orderbook.entity.order.model

import java.math.BigDecimal
import java.time.LocalDateTime

abstract class Order(
    val type: Type,
    val price: BigDecimal,
    var size: Int,
    val creationDate: LocalDateTime,
    val walletId: Long,
    private var state: State = State.IN_TRADE,
    val id: Long? = null
) {

    init {
        if(price < BigDecimal.ZERO || size < 0) {
            throw IllegalArgumentException("Invalid order values (price or size)")
        }
    }

    fun getState(): State {
        return this.state
    }

    fun getAllSizesAndCloseOrder(): Int {
        val allSizes = this.size
        this.state = State.DONE
        this.size = 0

        return allSizes
    }

    enum class Type {
        SELL, BUY
    }

    enum class State {
        IN_TRADE, CANCELLED, DONE
    }
}