package com.meli.orderbook.entity.order.gateway

import com.meli.orderbook.entity.order.model.Order

interface OrderCommandGateway {

    fun create(order: Order): Order
    fun update(order: Order)
}