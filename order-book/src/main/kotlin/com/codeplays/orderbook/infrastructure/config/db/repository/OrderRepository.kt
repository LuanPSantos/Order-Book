package com.codeplays.orderbook.infrastructure.config.db.repository

import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.State
import com.codeplays.orderbook.entity.order.model.Order.State.TRADING
import com.codeplays.orderbook.infrastructure.config.db.schema.OrderSchema
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderRepository : JpaRepository<OrderSchema, Long> {

    @Query(
        "SELECT order FROM OrderSchema order " +
                "WHERE order.state = :state " +
                "AND order.type = :type"
    )
    fun findOrdersByStateAndType(state: State, type: Order.Type): java.util.List<OrderSchema>

    @Query(
        "SELECT order FROM OrderSchema order " +
                "WHERE order.id = :id"
    )
    fun findOrderById(id: Long): OrderSchema

    @Query(
        "SELECT order FROM OrderSchema order " +
                "WHERE order.walletId = :walletId " +
                "AND order.state = :state"
    )
    fun findAllOrdersInTradeByWallet(walletId: Long, state: State = TRADING): java.util.List<OrderSchema>

}