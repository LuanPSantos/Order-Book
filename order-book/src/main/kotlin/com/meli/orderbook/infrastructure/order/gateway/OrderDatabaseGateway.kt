package com.meli.orderbook.infrastructure.order.gateway

import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.infrastructure.config.db.repository.OrderRepository
import com.meli.orderbook.infrastructure.config.db.schema.OrderSchema
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OrderDatabaseGateway(
    private val orderRepository: OrderRepository
) : OrderQueryGateway, OrderBookQueryGateway, OrderCommandGateway {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun get(): OrderBook {
        log.info("m=get")

        val asks = orderRepository
            .findOrdersByStateAndType(IN_TRADE, SELL)
            .map { Order(it.walletId!!, SELL, it.price!!, it.size!!, it.creationDate!!, it.state!!, it.id) }

        val bids = orderRepository.findOrdersByStateAndType(IN_TRADE, BUY)
            .map { Order(it.walletId!!, BUY, it.price!!, it.size!!, it.creationDate!!, it.state!!, it.id) }

        return OrderBook(asks, bids)
    }

    override fun create(order: Order): Order {
        log.info("m=create, order=$order")

        val schema = orderRepository.save(
            OrderSchema(
                order.id,
                order.type,
                order.price,
                order.size,
                order.creationDate,
                order.walletId,
                order.state
            )
        )

        return Order(
            schema.walletId!!,
            schema.type!!,
            schema.price!!,
            schema.size!!,
            schema.creationDate!!,
            schema.state!!,
            schema.id
        )
    }

    override fun update(order: Order) {
        log.info("m=update, order=$order")

        orderRepository.save(
            OrderSchema(
                order.id,
                order.type,
                order.price,
                order.size,
                order.creationDate,
                order.walletId,
                order.state
            )
        )
    }

    override fun findById(orderId: Long): Order {
        log.info("m=findById, orderId=$orderId")

        val schema = orderRepository.findOrderById(orderId)

        return Order(
            schema.walletId!!,
            schema.type!!,
            schema.price!!,
            schema.size!!,
            schema.creationDate!!,
            schema.state!!,
            schema.id
        )
    }

    override fun findAllOrdersInTradeByWallet(walletId: Long): List<Order> {
        return orderRepository.findAllOrdersInTradeByWallet(walletId)
            .map {
                Order(
                    it.walletId!!,
                    it.type!!,
                    it.price!!,
                    it.size!!,
                    it.creationDate!!,
                    it.state!!,
                    it.id
                )
            }
    }
}