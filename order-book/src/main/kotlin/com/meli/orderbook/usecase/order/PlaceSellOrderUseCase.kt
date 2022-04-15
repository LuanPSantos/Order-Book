package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.order.service.CreateOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlaceSellOrderUseCase(
    private val orderBookQueryGateway: OrderBookQueryGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val createOrderService: CreateOrderService,
    private val tradeService: TradeService
) {

    fun execute(input: Input) {

        val orderBook = orderBookQueryGateway.get()
        val sellOrder = createOrderService.createSellOrder(SellOrder(input.price, input.size, input.walletId))

        sellOrder.enableToTrade()

        val matchedBuyOrders = orderBook.findMatchingBuyOrders(sellOrder)

        tradeService.executeSell(sellOrder, matchedBuyOrders)

        orderCommandGateway.update(sellOrder)
    }

    data class Input(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}