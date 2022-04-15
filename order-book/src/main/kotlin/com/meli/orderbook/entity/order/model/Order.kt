package com.meli.orderbook.entity.order.model

import java.math.BigDecimal
import java.time.LocalDateTime

abstract class Order(
    val type: Type,
    val price: BigDecimal,
    size: Int,
    val creationDate: LocalDateTime,
    val walletId: Long,
    state: State = State.CREATING,
    val id: Long? = null
) {

    var state: State = state
        private set

    var size: Int = size
        private set

    init {
        if (price < BigDecimal.ZERO || size < 0) {
            throw IllegalArgumentException("Invalid order values (price or size)")
        }
    }

    fun close(): Order {
        this.state = State.CLOSED
        return this
    }

    fun cancel(): Order {
        this.state = State.CANCELLED
        return this
    }

    fun enableToTrade() {
        this.state = State.IN_TRADE
    }

    fun subtractAllSize(): Int {
        close()

        val allSizes = this.size
        this.size = 0

        return allSizes
    }

    fun subractSizes(sizes: Int) {
        if (sizes < 0) {
            throw IllegalArgumentException("Subtract negative value not allowed")
        }

        this.size -= sizes

        if(this.size == 0) {
            close()
        }
    }

    enum class Type {
        SELL, BUY
    }

    enum class State {
        CREATING, IN_TRADE, CANCELLED, CLOSED
    }
}