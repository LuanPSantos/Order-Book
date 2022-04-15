package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.order.service.CreateSellOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import java.math.BigDecimal

class PlaceSellOrderUseCase(
    private val orderBookQueryGateway: OrderBookQueryGateway,
    private val orderCommandGateway: OrderCommandGateway,
    private val createSellOrderService: CreateSellOrderService,
    private val tradeService: TradeService
) {

    fun execute(input: Input) {

        val orderBook = orderBookQueryGateway.get()

        val sellOrder = createSellOrderService.create(SellOrder(input.price, input.size, input.walletId))

        val matchedBuyOrders = orderBook.findMatchingBuyOrders(sellOrder).iterator()
        sellOrder.enableToTrade()

        while (matchedBuyOrders.hasNext()) {
            val buyOrder = matchedBuyOrders.next()
            if (sellOrder.canTradeWith(buyOrder)) {
                tradeService.executeSell(sellOrder, buyOrder)
            }
        }

//        if (matchedBuyOrders.isNotEmpty()) {
//            for (buyOrder in matchedBuyOrders) {
//                if (sellOrder.canTradeWith(buyOrder)) {
//                    tradeService.executeSell(sellOrder, buyOrder)
//                }
//            }
//        }

        orderCommandGateway.update(sellOrder)
    }

    data class Input(
        val walletId: Long,
        val size: Int,
        val price: BigDecimal
    )
}