package com.codeplays.orderbook.infrastructure.trade.gateway

import com.codeplays.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.codeplays.orderbook.entity.trade.gateway.TradeHistoryQueryGateway
import com.codeplays.orderbook.entity.trade.model.Trade
import com.codeplays.orderbook.infrastructure.config.db.repository.TradeRepository
import com.codeplays.orderbook.infrastructure.config.db.schema.TradeSchema
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
                trade.saleOrderId,
                trade.purchaseOrderId,
                trade.saleWalletId,
                trade.purchaseWalletId,
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
                id = it.id,
                saleOrderId = it.saleOrderId,
                purchaseOrderId = it.purchaseOrderId,
                saleWalletId = it.sellerWalletId,
                purchaseWalletId = it.buyerWalletId,
                type = it.type,
                size = it.size,
                price = it.price,
                change = it.changeMoney,
                creationDate = it.creationDate
            )
        }.toList()
    }
}