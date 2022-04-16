package com.meli.orderbook.entity.trade.gateway

import com.meli.orderbook.entity.trade.model.Trade

interface TradeHistoryQueryGateway {

    fun getHistory(pageNumber: Int, pageSize: Int): List<Trade>
}