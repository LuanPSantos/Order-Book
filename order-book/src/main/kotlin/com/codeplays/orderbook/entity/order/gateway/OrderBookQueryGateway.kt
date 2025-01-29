package com.codeplays.orderbook.entity.order.gateway

import com.codeplays.orderbook.entity.order.model.OrderBook

interface OrderBookQueryGateway {

    fun get(): OrderBook
}