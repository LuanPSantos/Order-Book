package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDateTime.now

class SellOrder(
    price: BigDecimal,
    size: Int,
    walletId: Long,
    creationDate: LocalDateTime = now(),
    id: Long? = null
) : Order(Type.SELL, price, size, creationDate, walletId, id = id) {

    fun canTradeWith(buyOrder: BuyOrder): Boolean {
        return buyOrder.size > 0 && buyOrder.price >= this.price && this.getState() == IN_TRADE
    }
}