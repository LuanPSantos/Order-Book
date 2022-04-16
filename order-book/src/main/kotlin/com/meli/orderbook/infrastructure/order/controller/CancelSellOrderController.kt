package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.usecase.order.CancelOrderUseCase.Input
import com.meli.orderbook.usecase.order.CancelSellOrderUseCase
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CancelSellOrderController(
    private val cancelSellOrderUseCase: CancelSellOrderUseCase
) {

    @DeleteMapping("api/v1/order-books/asks/{id}")
    fun cancelSellOrder(@PathVariable id: Long) {
        cancelSellOrderUseCase.execute(Input(id))
    }
}