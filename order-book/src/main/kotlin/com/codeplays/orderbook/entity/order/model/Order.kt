package com.codeplays.orderbook.entity.order.model

import com.codeplays.orderbook.entity.order.model.Order.State.TRADING
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDateTime

open class Order(
    val id: Long? = null,
    val walletId: Long,
    val type: Type,
    val price: BigDecimal,
    size: Int,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    state: State = State.CREATING,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

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
        this.state = TRADING
    }

    fun subtractAllSize(): Int {
        close()

        val allSizes = this.size
        this.size = 0

        return allSizes
    }

    fun subractSize(size: Int) {
        log.info("m=subractSize, size=$size, orderId=${this.id}")
        if (size < 0) {
            throw IllegalArgumentException("Subtract negative value not allowed")
        }

        this.size -= size

        if (this.size == 0) {
            close()
        }
    }

    fun canTradeWith(otherOrder: Order): Boolean {
        log.info("m=canTradeWith, otherOrderId=${otherOrder.id}, orderId=${this.id}")
        return when (type) {
            Type.PURCHASE -> otherOrder.size > 0 && otherOrder.price <= this.price && this.state == TRADING && otherOrder.state == TRADING
            Type.SALE -> otherOrder.size > 0 && otherOrder.price >= this.price && this.state == TRADING && otherOrder.state == TRADING
        }
    }

    override fun toString(): String {
        return "Order(type=$type, price=$price, creationDate=$creationDate, walletId=$walletId, id=$id, state=$state, size=$size)"
    }

    enum class Type {
        SALE, PURCHASE
    }

    enum class State {
        CREATING, TRADING, CANCELLED, CLOSED
    }
}