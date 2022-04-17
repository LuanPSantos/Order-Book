package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.usecase.order.CancelOrderUseCase.Input
import com.meli.orderbook.usecase.order.CancelSellOrderUseCase
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CancelSellOrderController(
    private val cancelSellOrderUseCase: CancelSellOrderUseCase
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @DeleteMapping("api/v1/order-books/asks/{id}")
    fun cancelSellOrder(@PathVariable id: Long) {
        log.info("m=cancelSellOrder, orderId=$id")

        cancelSellOrderUseCase.execute(Input(id))
    }
}