package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.service.CreateOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import java.math.BigDecimal

class PlaceBuyOrderUseCase (
    private val orderBookQueryGateway: OrderBookQueryGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val createOrderService: CreateOrderService,
    private val tradeService: TradeService
) {

    fun execute(input: Input) {

        val orderBook = orderBookQueryGateway.get()
        val buyOrder = createOrderService.create(BuyOrder(input.price, input.size, input.walletId))

        buyOrder.enableToTrade()

        val matchingSellOrders = orderBook.findMatchingSellOrders(buyOrder)

        tradeService.executeBuy(buyOrder, matchingSellOrders)

        orderCommandGateway.update(buyOrder)
    }

    data class Input(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}