package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.OrderBook
import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway

class GetOrderBookUseCase(
    private val orderBookQueryGateway: OrderBookQueryGateway
) {

    fun execute(): Output {

        val orderBook = orderBookQueryGateway.get()

        return Output(orderBook)
    }

    data class Output(
        val orderBook: OrderBook
    )
}