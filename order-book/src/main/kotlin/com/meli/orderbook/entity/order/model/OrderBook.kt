package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import org.slf4j.LoggerFactory

class OrderBook(
    asks: List<Order>,
    bids: List<Order>
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    val asks = asks.sortedWith(compareBy({ it.price }, { it.creationDate }))
    val bids = bids.sortedWith(compareByDescending<Order> { it.price }.thenBy { it.creationDate })

    fun findMatchingOrders(order: Order): List<Order> {
        log.info("m=findMatchingOrders, orderId=${order.id}")
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