package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.service.CreateBuyOrderService
import com.meli.orderbook.entity.order.service.CreateOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlaceBuyOrderUseCase(
    private val createOrderService: CreateBuyOrderService,
    orderBookQueryGateway: OrderBookQueryGateway,
    orderCommandGateway: OrderCommandGateway,
    tradeService: TradeService
) : PlaceOrderUseCase(orderBookQueryGateway, orderCommandGateway, tradeService) {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun createOrder(walletId: Long, size: Int, price: BigDecimal): Order {
        log.info("m=createOrder, walletId=$walletId, size=$size, price=$price")

        return createOrderService.createOrder(Order(walletId, BUY, price, size))
    }
}