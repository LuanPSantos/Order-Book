package com.meli.orderbook.entity.transaction.model

import com.meli.orderbook.entity.order.model.Order
import java.math.BigDecimal

data class Transaction(
    val sellerWalletId: Long,
    val buyerWalletId: Long,
    val type: Order.Type,
    val size: Int,
    val price: BigDecimal,
    val id: Long? = null
)