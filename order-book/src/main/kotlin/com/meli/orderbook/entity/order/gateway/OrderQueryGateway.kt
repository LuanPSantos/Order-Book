package com.meli.orderbook.entity.order.gateway

import com.meli.orderbook.entity.order.model.Order

interface OrderQueryGateway {

    fun findById(orderId: Long): Order
}