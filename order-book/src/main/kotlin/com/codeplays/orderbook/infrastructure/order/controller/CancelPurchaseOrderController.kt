package com.codeplays.orderbook.infrastructure.order.controller

import com.codeplays.orderbook.usecase.order.CancelPurchaseOrderUseCase
import com.codeplays.orderbook.usecase.order.CancelOrderUseCase.Input
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CancelPurchaseOrderController(
    private val cancelPurchaseOrderUseCase: CancelPurchaseOrderUseCase
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @DeleteMapping("api/v1/order-books/bids/{orderId}")
    fun cancelPurchaseOrder(@PathVariable orderId: Long) {
        log.info("m=cancelPurchaseOrder, orderId=$orderId")

        cancelPurchaseOrderUseCase.execute(Input(orderId))
    }
}