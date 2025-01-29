package com.codeplays.orderbook.usecase.order

import com.codeplays.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
import com.codeplays.orderbook.entity.order.service.CreatePurchaseOrderService
import com.codeplays.orderbook.entity.trade.service.TradeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlacePurchaseOrderUseCase(
    private val createOrderService: CreatePurchaseOrderService,
    orderBookQueryGateway: OrderBookQueryGateway,
    orderCommandGateway: OrderCommandGateway,
    tradeService: TradeService
) : PlaceOrderUseCase(orderBookQueryGateway, orderCommandGateway, tradeService) {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun createOrder(walletId: Long, size: Int, price: BigDecimal): Order {
        log.info("m=createOrder, walletId=$walletId, size=$size, price=$price")

        return createOrderService.createOrder(Order(walletId = walletId, type = PURCHASE, price = price, size = size))
    }
}