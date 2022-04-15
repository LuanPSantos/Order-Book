package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.usecase.order.GetOrderBookUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GetOrderBookController(
    private val getOrderBookUseCase: GetOrderBookUseCase
) {

    @GetMapping("api/v1/order-books")
    fun getOrderBook(): Response {
        val output = getOrderBookUseCase.execute()

        return Response(output.orderBook)
    }

    data class Response(
        val orderBook: OrderBook
    )
}