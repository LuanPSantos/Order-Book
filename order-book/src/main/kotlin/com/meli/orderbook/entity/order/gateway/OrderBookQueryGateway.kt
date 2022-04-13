package com.meli.orderbook.entity.order.gateway

import com.meli.orderbook.entity.order.OrderBook

interface OrderBookQueryGateway {

    fun get(): OrderBook
}