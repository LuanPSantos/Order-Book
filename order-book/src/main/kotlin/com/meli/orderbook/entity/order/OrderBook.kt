package com.meli.orderbook.entity.order

import com.meli.orderbook.entity.order.Order.Type.BUY
import com.meli.orderbook.entity.order.Order.Type.SELL

class OrderBook(
    val asks: List<Order>,
    val bids: List<Order>
) {

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