package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.usecase.order.CancelBuyOrderUseCase
import com.meli.orderbook.usecase.order.CancelOrderUseCase.Input
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CancelBuyOrderController(
    private val cancelBuyOrderUseCase: CancelBuyOrderUseCase
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @DeleteMapping("api/v1/order-books/bids/{orderId}")
    fun cancelBuyOrder(@PathVariable id: Long) {
        log.info("m=cancelBuyOrder, orderId=$id")

        cancelBuyOrderUseCase.execute(Input(id))
    }
}