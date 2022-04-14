package com.meli.orderbook.entity.order.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDateTime.now


class BuyOrder(
    price: BigDecimal,
    size: Int,
    walletId: Long,
    creationDate: LocalDateTime = now(),
    id: Long? = null
) : Order(Type.BUY, price, size, creationDate, walletId, id = id) {

}