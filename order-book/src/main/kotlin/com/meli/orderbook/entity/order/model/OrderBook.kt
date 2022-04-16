package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL

class OrderBook(
    asks: List<Order>,
    bids: List<Order>
) {
    val asks = asks.sortedWith(compareBy({ it.price }, { it.creationDate }))
    val bids = bids.sortedWith(compareByDescending<Order> { it.price }.thenBy { it.creationDate })

    fun findMatchingOrders(order: Order): List<Order> {
        return when (order.type) {
            BUY -> findMatchingSellOrders(order)
            SELL -> findMatchingBuyOrders(order)
        }
    }

    private fun findMatchingSellOrders(order: Order): List<Order> {
        return asks.filter { it.price <= order.price }
    }

    private fun findMatchingBuyOrders(order: Order): List<Order> {
        return bids.filter { it.price >= order.price }
    }
}