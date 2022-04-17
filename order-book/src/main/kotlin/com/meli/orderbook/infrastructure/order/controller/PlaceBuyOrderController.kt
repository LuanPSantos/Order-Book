package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.usecase.order.PlaceBuyOrderUseCase
import com.meli.orderbook.usecase.order.PlaceOrderUseCase.Input
import org.slf4j.LoggerFactory

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class PlaceBuyOrderController(
    private val placeBuyOrderUseCase: PlaceBuyOrderUseCase
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("api/v1/order-books/bids")
    fun placeBuyOrder(@RequestBody request: Request) {
        log.info("m=placeBuyOrder, walletId=${request.walletId}, size=${request.size}, price=${request.price}")

        placeBuyOrderUseCase.execute(Input(request.walletId, request.size, request.price))
    }

    data class Request(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}