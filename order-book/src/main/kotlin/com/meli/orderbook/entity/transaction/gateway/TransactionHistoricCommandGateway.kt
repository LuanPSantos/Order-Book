package com.meli.orderbook.entity.transaction.gateway

import com.meli.orderbook.entity.transaction.model.Transaction

interface TransactionHistoricCommandGateway {

    fun register(transaction: Transaction)
}