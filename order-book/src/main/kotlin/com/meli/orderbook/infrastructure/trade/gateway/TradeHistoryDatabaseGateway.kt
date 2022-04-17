package com.meli.orderbook.infrastructure.trade.gateway

import com.meli.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.meli.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.infrastructure.config.db.repository.TradeRepository
import com.meli.orderbook.infrastructure.config.db.schema.TradeSchema
import org.slf4j.LoggerFactory

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class TradeHistoryDatabaseGateway(
    private val tradeRepository: TradeRepository
) : TradeHistoryCommandGateway, TradeHistoryQueryGateway {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun register(trade: Trade) {
        log.info("m=register, trade=$trade")

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
        log.info("m=getHistory, pageNumber=$pageNumber, pageSize=$pageSize")

        return tradeRepository.findAll(Pageable.ofSize(pageSize).withPage(pageNumber)).map {
            Trade(
                it.sellOrderId!!,
                it.buyerOrderId!!,
                it.sellerWalletId!!,
                it.buyerWalletId!!,
                it.type!!,
                it.size!!,
                it.price!!,
                it.changeMoney!!,
                it.creationDate!!,
                it.id
            )
        }.toList()
    }
}