package com.codeplays.orderbook.entity.order.gateway

import com.codeplays.orderbook.entity.order.model.Order

interface OrderQueryGateway {

    fun findById(orderId: Long): Order
    fun findAllOrdersInTradeByWalletId(walletId: Long): List<Order>
}