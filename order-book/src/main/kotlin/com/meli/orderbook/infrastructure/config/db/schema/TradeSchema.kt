package com.meli.orderbook.infrastructure.config.db.schema

import com.meli.orderbook.entity.order.model.Order
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class TradeSchema(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val sellOrderId: Long? = null,
    val buyerOrderId: Long? = null,
    @field:Enumerated(EnumType.STRING)
    val type: Order.Type? = null,
    val size: Int? = null,
    val price: BigDecimal? = null,
    val change: BigDecimal? = null,
    val creationDate: LocalDateTime? = null,

)