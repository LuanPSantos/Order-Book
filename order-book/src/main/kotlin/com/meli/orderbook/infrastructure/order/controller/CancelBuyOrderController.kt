package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.usecase.order.CancelBuyOrderUseCase
import com.meli.orderbook.usecase.order.CancelOrderUseCase.Input
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CancelBuyOrderController(
    private val cancelBuyOrderUseCase: CancelBuyOrderUseCase
) {

    @DeleteMapping("api/v1/order-books/bids/{id}")
    fun cancelSellOrder(@PathVariable id: Long) {
        cancelBuyOrderUseCase.execute(Input(id))
    }
}