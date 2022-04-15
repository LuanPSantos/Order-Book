package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import org.springframework.stereotype.Service

@Service
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