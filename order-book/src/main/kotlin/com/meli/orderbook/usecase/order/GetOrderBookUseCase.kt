package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GetOrderBookUseCase(
    private val orderBookQueryGateway: OrderBookQueryGateway
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(): Output {
        log.info("m=execute")

        val orderBook = orderBookQueryGateway.get()

        return Output(orderBook)
    }

    data class Output(
        val orderBook: OrderBook
    )
}