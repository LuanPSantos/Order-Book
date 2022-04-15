package com.meli.orderbook.entity.trade.model

import com.meli.orderbook.entity.order.model.Order
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class Trade(
    val sellOrderId: Long,
    val buyerOrderId: Long,
    val type: Order.Type,
    val size: Int,
    val price: BigDecimal,
    val change: BigDecimal,
    val creationDate: LocalDateTime = now(),
    val id: Long? = null
)