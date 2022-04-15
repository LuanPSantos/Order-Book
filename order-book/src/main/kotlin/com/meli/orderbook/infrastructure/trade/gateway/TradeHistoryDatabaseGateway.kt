package com.meli.orderbook.infrastructure.trade.gateway

import com.meli.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.meli.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.infrastructure.config.db.repository.TradeRepository
import com.meli.orderbook.infrastructure.config.db.schema.TradeSchema
import org.springframework.stereotype.Component

@Component
class TradeHistoryDatabaseGateway(
    private val tradeRepository: TradeRepository
) : TradeHistoryCommandGateway, TradeHistoryQueryGateway {
    override fun register(trade: Trade) {
        tradeRepository.save(
            TradeSchema(
                trade.id,
                trade.sellOrderId,
                trade.buyerOrderId,
                trade.type,
                trade.size,
                trade.price,
                trade.change,
                trade.creationDate
            )
        )
    }

    override fun getHistory(start: Int, pageSize: Int): List<Trade> {
        TODO("Not yet implemented")
    }
}