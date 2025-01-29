package com.codeplays.orderbook.usecase.order

import com.codeplays.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.trade.service.TradeService
import org.slf4j.LoggerFactory
import java.math.BigDecimal

abstract class PlaceOrderUseCase(
    private val orderBookQueryGateway: OrderBookQueryGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val tradeService: TradeService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(input: Input) {
        log.info("m=execute, walletId=${input.walletId}, size=${input.size}, price=${input.price}")

        val orderBook = orderBookQueryGateway.get()
        val order = createOrder(input.walletId, input.size, input.price)

        order.enableToTrade()

        val matchingOrders = orderBook.findMatchingOrders(order)

        tradeService.execute(order, matchingOrders)

        orderCommandGateway.update(order)
    }

    protected abstract fun createOrder(walletId: Long, size: Int, price: BigDecimal): Order

    data class Input(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}