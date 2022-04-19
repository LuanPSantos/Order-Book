package com.meli.orderbook.infrastructure.config.db.schema

import com.meli.orderbook.entity.order.model.Order
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class OrderSchema(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @field:Enumerated(EnumType.STRING)
    val type: Order.Type,
    val price: BigDecimal,
    val size: Int,
    val creationDate: LocalDateTime,
    val walletId: Long,
    @field:Enumerated(EnumType.STRING)
    val state: Order.State
)