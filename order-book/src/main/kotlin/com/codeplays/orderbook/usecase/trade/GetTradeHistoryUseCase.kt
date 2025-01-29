package com.codeplays.orderbook.usecase.trade

import com.codeplays.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.codeplays.orderbook.entity.trade.model.Trade
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GetTradeHistoryUseCase(
    private val tradeHistoryQueryGateway: TradeHistoryQueryGateway
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(input: Input): Output {
        log.info("m=execute, pageNumber=${input.pageNumber}, pageSize=${input.pageSize}")

        val history = tradeHistoryQueryGateway.getHistory(input.pageNumber, input.pageSize)

        return Output(history)
    }

    data class Input(
        val pageNumber: Int,
        val pageSize: Int
    )

    data class Output(
        val trades: List<Trade>
    )
}