package com.meli.orderbook.infrastructure.order.controller

import com.meli.orderbook.usecase.order.PlaceSellOrderUseCase
import com.meli.orderbook.usecase.order.PlaceSellOrderUseCase.Input
import org.slf4j.LoggerFactory.getLogger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class PlaceSellOrderController(
    private val placeSellOrderUseCase: PlaceSellOrderUseCase
) {

    private val log = getLogger(this::class.java)

    @PostMapping("api/v1/order-books/asks")
    fun placeSellOrder(@RequestBody request: Request) {
        log.info("m=placeSellOrder")
        placeSellOrderUseCase.execute(Input(request.walletId, request.size, request.price))
    }

    data class Request(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}