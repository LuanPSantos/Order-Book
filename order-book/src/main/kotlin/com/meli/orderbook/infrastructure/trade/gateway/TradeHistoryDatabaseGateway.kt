package com.meli.orderbook.infrastructure.trade.gateway

import com.meli.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.meli.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.infrastructure.config.db.repository.TradeRepository
import com.meli.orderbook.infrastructure.config.db.schema.TradeSchema

import org.springframework.data.domain.Pageable
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
                trade.sellerWalletId,
                trade.buyerWalletId,
                trade.type,
                trade.size,
                trade.price,
                trade.change,
                trade.creationDate
            )
        )
    }

    override fun getHistory(pageNumber: Int, pageSize: Int): List<Trade> {
        return tradeRepository.findAll(Pageable.ofSize(pageSize).withPage(pageNumber)).map {
            Trade(
                it.sellOrderId!!,
                it.buyerOrderId!!,
                it.sellerWalletId!!,
                it.buyerWalletId!!,
                it.type!!,
                it.size!!,
                it.price!!,
                it.change!!,
                it.creationDate!!,
                it.id
            )
        }.toList()
    }
}