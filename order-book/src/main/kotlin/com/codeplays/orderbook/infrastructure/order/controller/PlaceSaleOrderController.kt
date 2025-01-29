package com.codeplays.orderbook.infrastructure.order.controller

import com.codeplays.orderbook.usecase.order.PlaceOrderUseCase.Input
import com.codeplays.orderbook.usecase.order.PlaceSaleOrderUseCase
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class PlaceSaleOrderController(
    private val placeSaleOrderUseCase: PlaceSaleOrderUseCase
) {

    private val log = getLogger(this::class.java)

    @PostMapping("api/v1/order-books/asks")
    @ResponseStatus(HttpStatus.CREATED)
    fun placeSaleOrder(@RequestBody request: Request) {
        log.info("m=placeSaleOrder, walletId=${request.walletId}, size=${request.size}, price=${request.price}")

        placeSaleOrderUseCase.execute(Input(request.walletId, request.size, request.price))
    }

    data class Request(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}