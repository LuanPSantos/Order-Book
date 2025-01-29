package com.codeplays.orderbook.infrastructure.order.gateway

import com.codeplays.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.gateway.OrderQueryGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.State.TRADING
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.order.model.OrderBook
import com.codeplays.orderbook.infrastructure.config.db.repository.OrderRepository
import com.codeplays.orderbook.infrastructure.config.db.schema.OrderSchema
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
            .findOrdersByStateAndType(TRADING, SALE)
            .map {
                Order(
                    id = it.id,
                    walletId = it.walletId,
                    type = it.type,
                    price = it.price,
                    size = it.size,
                    creationDate = it.creationDate,
                    state = it.state
                )
            }

        val bids = orderRepository.findOrdersByStateAndType(TRADING, PURCHASE)
            .map {
                Order(
                    id = it.id,
                    walletId = it.walletId,
                    type = it.type,
                    price = it.price,
                    size = it.size,
                    creationDate = it.creationDate,
                    state = it.state
                )
            }

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
            id = schema.id,
            walletId = schema.walletId,
            type = schema.type,
            price = schema.price,
            size = schema.size,
            creationDate = schema.creationDate,
            state = schema.state
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
            id = schema.id,
            walletId = schema.walletId,
            type = schema.type,
            price = schema.price,
            size = schema.size,
            creationDate = schema.creationDate,
            state = schema.state
        )
    }

    override fun findAllOrdersInTradeByWalletId(walletId: Long): List<Order> {
        return orderRepository.findAllOrdersInTradeByWallet(walletId)
            .map {
                Order(
                    id = it.id,
                    walletId = it.walletId,
                    type = it.type,
                    price = it.price,
                    size = it.size,
                    creationDate = it.creationDate,
                    state = it.state
                )
            }
    }
}