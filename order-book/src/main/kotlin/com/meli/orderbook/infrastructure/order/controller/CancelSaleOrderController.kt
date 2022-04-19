package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.usecase.order.CancelOrderUseCase.Input
import com.meli.orderbook.usecase.order.CancelSaleOrderUseCase
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CancelSaleOrderController(
    private val cancelSaleOrderUseCase: CancelSaleOrderUseCase
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @DeleteMapping("api/v1/order-books/asks/{orderId}")
    fun cancelSaleOrder(@PathVariable orderId: Long) {
        log.info("m=cancelSaleOrder, orderId=$orderId")

        cancelSaleOrderUseCase.execute(Input(orderId))
    }
}