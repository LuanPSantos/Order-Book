package com.meli.orderbook.entity

import com.meli.orderbook.entity.Order.Type.BUY
import com.meli.orderbook.entity.Order.Type.SELL

class OrderBook(
    asks: List<Order>,
    bids: List<Order>
) {

    val asks = asks.sortedWith(compareBy({ it.price }, { it.creationDate }))
    val bids = bids.sortedWith(compareByDescending<Order> { it.price }.thenBy { it.creationDate })

    fun findMatchingOrders(order: Order): List<Order> {
        return when (order.type) {
            BUY -> findMatchingSellsOrder(order)
            SELL -> findMatchingBuysOrder(order)
        }
    }

    private fun findMatchingSellsOrder(order: Order): List<Order> {
        return asks.filter { it.price == order.price }
    }

    private fun findMatchingBuysOrder(order: Order): List<Order> {
        return bids.filter { it.price == order.price }
    }
}