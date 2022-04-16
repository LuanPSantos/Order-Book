package com.meli.orderbook.infrastructure.config.db.repository

import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.infrastructure.config.db.schema.OrderSchema
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderRepository : JpaRepository<OrderSchema, Long> {

    @Query(
        "SELECT order FROM OrderSchema order " +
                "WHERE order.state = :state " +
                "AND order.type = :type"
    )
    fun findOrdersByStateAndType(state: Order.State, type: Order.Type): List<OrderSchema>

    @Query(
        "SELECT order FROM OrderSchema order " +
                "WHERE order.id = :id"
    )
    fun findOrderById(id: Long): OrderSchema
}