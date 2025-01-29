package com.codeplays.orderbook.entity.trade.gateway

import com.codeplays.orderbook.entity.trade.model.Trade

interface TradeHistoryCommandGateway {

    fun register(trade: Trade)
}