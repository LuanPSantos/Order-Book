package com.codeplays.orderbook.infrastructure.config.db.schema

import com.codeplays.orderbook.entity.order.model.Order
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class TradeSchema(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val saleOrderId: Long,
    val purchaseOrderId: Long,
    val sellerWalletId: Long,
    val buyerWalletId: Long,
    @field:Enumerated(EnumType.STRING)
    val type: Order.Type,
    val size: Int,
    val price: BigDecimal,
    val changeMoney: BigDecimal,
    val creationDate: LocalDateTime
)