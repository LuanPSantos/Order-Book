package com.codeplays.orderbook.entity.trade.gateway

import com.codeplays.orderbook.entity.trade.model.Trade

interface TradeHistoryQueryGateway {

    fun getHistory(pageNumber: Int, pageSize: Int): List<Trade>
}