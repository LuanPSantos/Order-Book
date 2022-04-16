package com.meli.orderbook.entity.order.model

import java.math.BigDecimal
import java.time.LocalDateTime

open class Order(
    val walletId: Long,
    val type: Type,
    val price: BigDecimal,
    size: Int,
    val creationDate: LocalDateTime = LocalDateTime.now(),
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

        if (this.size == 0) {
            close()
        }
    }

    fun canTradeWith(otherOrder: Order): Boolean {
        return when (type) {
            Type.BUY -> otherOrder.size > 0 && otherOrder.price <= this.price && this.state == State.IN_TRADE && otherOrder.state == State.IN_TRADE
            Type.SELL -> otherOrder.size > 0 && otherOrder.price >= this.price && this.state == State.IN_TRADE && otherOrder.state == State.IN_TRADE
        }
    }

    override fun toString(): String {
        return "Order(type=$type, price=$price, creationDate=$creationDate, walletId=$walletId, id=$id, state=$state, size=$size)"
    }


    enum class Type {
        SELL, BUY
    }

    enum class State {
        CREATING, IN_TRADE, CANCELLED, CLOSED
    }
}