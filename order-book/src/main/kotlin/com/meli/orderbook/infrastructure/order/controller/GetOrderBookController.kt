package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.usecase.order.GetOrderBookUseCase
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GetOrderBookController(
    private val getOrderBookUseCase: GetOrderBookUseCase
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("api/v1/order-books")
    fun getOrderBook(): Response {
        log.info("m=getOrderBook")

        val output = getOrderBookUseCase.execute()

        return Response(output.orderBook)
    }

    data class Response(
        val orderBook: OrderBook
    )
}