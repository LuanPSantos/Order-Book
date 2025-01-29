package com.codeplays.orderbook.infrastructure.config.db.schema

import com.codeplays.orderbook.entity.order.model.Order
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

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