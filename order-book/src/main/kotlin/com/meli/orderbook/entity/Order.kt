package com.meli.orderbook.entity

import java.math.BigDecimal
import java.time.LocalDateTime

class Order(
    val id: Long,
    val type: Type,
    val price: BigDecimal, // nao negativo
    val size: Int, // nao negativo
    val creationDate: LocalDateTime,
    val walletId: Long,
    val state: State = State.IN_TRADE
) {

    enum class Type {
        SELL, BUY
    }

    enum class State {
        IN_TRADE, CANCELLED, DONE
    }
}