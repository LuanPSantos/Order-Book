package com.codeplays.orderbook.infrastructure.order.controller

import com.codeplays.orderbook.usecase.order.PlacePurchaseOrderUseCase
import com.codeplays.orderbook.usecase.order.PlaceOrderUseCase.Input
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class PlacePurchaseOrderController(
    private val placePurchaseOrderUseCase: PlacePurchaseOrderUseCase
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("api/v1/order-books/bids")
    @ResponseStatus(HttpStatus.CREATED)
    fun placePurchaseOrder(@RequestBody request: Request) {
        log.info("m=placePurchaseOrder, walletId=${request.walletId}, size=${request.size}, price=${request.price}")

        placePurchaseOrderUseCase.execute(Input(request.walletId, request.size, request.price))
    }

    data class Request(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}