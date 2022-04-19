package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.Type.SALE
import com.meli.orderbook.entity.order.service.CreateSaleOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlaceSaleOrderUseCase(
    private val createOrderService: CreateSaleOrderService,
    orderBookQueryGateway: OrderBookQueryGateway,
    orderCommandGateway: OrderCommandGateway,
    tradeService: TradeService
) : PlaceOrderUseCase(orderBookQueryGateway, orderCommandGateway, tradeService) {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun createOrder(walletId: Long, size: Int, price: BigDecimal): Order {
        log.info("m=createOrder, walletId=$walletId, size=$size, price=$price")

        return createOrderService.createOrder(Order(walletId = walletId, type = SALE, price = price, size = size))
    }
}