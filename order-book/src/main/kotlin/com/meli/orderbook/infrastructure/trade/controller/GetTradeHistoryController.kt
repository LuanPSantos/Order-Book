package com.meli.orderbook.infrastructure.trade.controller

import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.usecase.trade.GetTradeHistoryUseCase
import com.meli.orderbook.usecase.trade.GetTradeHistoryUseCase.Input
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GetTradeHistoryController(
    private val getTradeHistoryUseCase: GetTradeHistoryUseCase
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping("api/v1/trades")
    fun getTradeHistory(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): Response {
        log.info("getTradeHistory, pageNumber=$pageNumber, pageSize=$pageSize")

        val output = getTradeHistoryUseCase.execute(Input(pageNumber, pageSize))

        return Response(output.trades)
    }

    data class Response(
        val trades: List<Trade>
    )
}