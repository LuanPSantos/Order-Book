package com.meli.orderbook.entity.trade.gateway

import com.meli.orderbook.entity.trade.model.Trade

interface TradeHistoricCommandGateway {

    fun register(trade: Trade)
}