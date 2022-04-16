package com.meli.orderbook.infrastructure.trade.controller

import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.usecase.trade.GetTradeHistoryUseCase
import com.meli.orderbook.usecase.trade.GetTradeHistoryUseCase.Input
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GetTradeHistoryController(
    private val getTradeHistoryUseCase: GetTradeHistoryUseCase
) {

    @GetMapping("api/v1/trades")
    fun getTradeHistory(@RequestParam pageNumber: Int, @RequestParam pageSize: Int): Response {
        val output = getTradeHistoryUseCase.execute(Input(pageNumber, pageSize))

        return Response(output.trades)
    }

    data class Response(
        val trades: List<Trade>
    )
}