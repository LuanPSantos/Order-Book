package com.codeplays.orderbook.entity.trade.model

import com.codeplays.orderbook.entity.order.model.Order
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class Trade(
    val id: Long? = null,
    val saleOrderId: Long,
    val purchaseOrderId: Long,
    val saleWalletId: Long,
    val purchaseWalletId: Long,
    val type: Order.Type,
    val size: Int,
    val price: BigDecimal,
    val change: BigDecimal,
    val creationDate: LocalDateTime = now()
)