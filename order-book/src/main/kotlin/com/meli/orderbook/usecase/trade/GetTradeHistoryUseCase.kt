package com.meli.orderbook.usecase.trade

import com.meli.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.meli.orderbook.entity.trade.model.Trade

class GetTradeHistoryUseCase(
    private val tradeHistoryQueryGateway: TradeHistoryQueryGateway
) {

    fun execute(input: Input): Output {
        val history = tradeHistoryQueryGateway.getHistory(input.start, input.pageSize)

        return Output(history)
    }

    data class Input(
        val start: Int,
        val pageSize: Int
    )

    data class Output(
        val trades: List<Trade>
    )
}