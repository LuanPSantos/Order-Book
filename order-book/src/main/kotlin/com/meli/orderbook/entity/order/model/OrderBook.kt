package com.meli.orderbook.entity.order.model

class OrderBook(
    asks: List<SellOrder>,
    bids: List<BuyOrder>
) {
    val asks = asks.sortedWith(compareBy({ it.price }, { it.creationDate }))
    val bids = bids.sortedWith(compareByDescending<BuyOrder> { it.price }.thenBy { it.creationDate })

    fun findMatchingSellOrders(order: BuyOrder): List<SellOrder> {
        return asks.filter { it.price <= order.price }
    }

    fun findMatchingBuyOrders(order: SellOrder): List<BuyOrder> {
        return bids.filter { it.price >= order.price }
    }
}