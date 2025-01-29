package com.codeplays.orderbook.entity.order.gateway

import com.codeplays.orderbook.entity.order.model.Order

interface OrderCommandGateway {

    fun create(order: Order): Order
    fun update(order: Order)
}