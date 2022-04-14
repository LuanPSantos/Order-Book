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

    fun discountSize(size: Int) {
        if (size < 0 || size > this.size) {
            throw IllegalArgumentException("Invalid discount size")
        }

        this.size -= size

        if(this.size == 0) {
            this.state = State.DONE
        }
    }

    enum class Type {
        SELL, BUY
    }

    enum class State {
        IN_TRADE, CANCELLED, DONE
    }
}