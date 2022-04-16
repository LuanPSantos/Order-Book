package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.CREATING
import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.entity.order.service.CreateBuyOrderService
import com.meli.orderbook.entity.order.service.CreateOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import com.meli.orderbook.usecase.order.PlaceOrderUseCase.Input
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class PlaceBuyOrderUseCaseTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var orderBookQueryGateway: OrderBookQueryGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @MockK
    lateinit var createOrderService: CreateBuyOrderService

    @MockK
    lateinit var tradeService: TradeService

    @InjectMockKs
    lateinit var placeBuyOrderUseCase: PlaceBuyOrderUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should place a buy-order whith matching sell-orders in the order-book`() {

        val createBuyOrderSlot = slot<Order>()
        val buyOrderSlot = slot<Order>()
        val sellOrderSlot = slot<List<Order>>()
        val resolvedBuyOrderSlot = slot<Order>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.createOrder(capture(createBuyOrderSlot))
        } returns Order(7, BUY, BigDecimal("250"), 10, dateTime, id = 7)
        every { tradeService.execute(capture(buyOrderSlot), capture(sellOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedBuyOrderSlot)) } just Runs

        placeBuyOrderUseCase.execute(Input(7, 10, BigDecimal("250")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.createOrder(any()) }
        verify(exactly = 1) { tradeService.execute(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createBuyOrderSlot.captured.state)
        assertEquals(BigDecimal("250"), createBuyOrderSlot.captured.price)
        assertEquals(BUY, createBuyOrderSlot.captured.type)
        assertEquals(7, createBuyOrderSlot.captured.walletId)
        assertEquals(10, createBuyOrderSlot.captured.size)

        assertEquals(IN_TRADE, buyOrderSlot.captured.state)
        assertEquals(BigDecimal("250"), buyOrderSlot.captured.price)
        assertEquals(BUY, buyOrderSlot.captured.type)
        assertEquals(7, buyOrderSlot.captured.walletId)
        assertEquals(10, buyOrderSlot.captured.size)
        assertEquals(7, buyOrderSlot.captured.id)
        Assertions.assertNotNull(buyOrderSlot.captured.creationDate)

        assertEquals(IN_TRADE, sellOrderSlot.captured[0].state)
        assertEquals(BigDecimal("250"), sellOrderSlot.captured[0].price)
        assertEquals(SELL, sellOrderSlot.captured[0].type)
        assertEquals(3, sellOrderSlot.captured[0].walletId)
        assertEquals(3, sellOrderSlot.captured[0].size)
        assertEquals(3, sellOrderSlot.captured[0].id)
        Assertions.assertNotNull(sellOrderSlot.captured[0].creationDate)

        assertEquals(IN_TRADE, resolvedBuyOrderSlot.captured.state)
        assertEquals(BigDecimal("250"), resolvedBuyOrderSlot.captured.price)
        assertEquals(BUY, resolvedBuyOrderSlot.captured.type)
        assertEquals(7, resolvedBuyOrderSlot.captured.walletId)
        assertEquals(10, resolvedBuyOrderSlot.captured.size)
        assertEquals(7, resolvedBuyOrderSlot.captured.id)
        Assertions.assertNotNull(resolvedBuyOrderSlot.captured.creationDate)

    }

    @Test
    fun `Should place a buy-order in the order-book when has no matching sell-orders`() {
        val createBuyOrderSlot = slot<Order>()
        val buyOrderSlot = slot<Order>()
        val sellOrderSlot = slot<List<Order>>()
        val resolvedBuyOrderSlot = slot<Order>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.createOrder(capture(createBuyOrderSlot))
        } returns Order(7, BUY, BigDecimal("200"), 10, dateTime, id = 7)
        every { tradeService.execute(capture(buyOrderSlot), capture(sellOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedBuyOrderSlot)) } just Runs

        placeBuyOrderUseCase.execute(Input(7, 10, BigDecimal("200")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.createOrder(any()) }
        verify(exactly = 1) { tradeService.execute(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createBuyOrderSlot.captured.state)
        assertEquals(BigDecimal("200"), createBuyOrderSlot.captured.price)
        assertEquals(BUY, createBuyOrderSlot.captured.type)
        assertEquals(7, createBuyOrderSlot.captured.walletId)
        assertEquals(10, createBuyOrderSlot.captured.size)

        assertEquals(IN_TRADE, buyOrderSlot.captured.state)
        assertEquals(BigDecimal("200"), buyOrderSlot.captured.price)
        assertEquals(BUY, buyOrderSlot.captured.type)
        assertEquals(7, buyOrderSlot.captured.walletId)
        assertEquals(10, buyOrderSlot.captured.size)
        assertEquals(7, buyOrderSlot.captured.id)
        Assertions.assertNotNull(buyOrderSlot.captured.creationDate)

        assertEquals(0, sellOrderSlot.captured.size)

        assertEquals(IN_TRADE, resolvedBuyOrderSlot.captured.state)
        assertEquals(BigDecimal("200"), resolvedBuyOrderSlot.captured.price)
        assertEquals(BUY, resolvedBuyOrderSlot.captured.type)
        assertEquals(7, resolvedBuyOrderSlot.captured.walletId)
        assertEquals(10, resolvedBuyOrderSlot.captured.size)
        assertEquals(7, resolvedBuyOrderSlot.captured.id)
        Assertions.assertNotNull(resolvedBuyOrderSlot.captured.creationDate)
    }

    private fun createOrderBook(): OrderBook {
        return OrderBook(
            listOf(
                Order(1, SELL, BigDecimal("300"), 5, dateTime, IN_TRADE, 1),
                Order(3, SELL, BigDecimal("250"), 3, dateTime, IN_TRADE, 3)
            ),
            listOf(
                Order(2, BUY, BigDecimal("110"), 4, dateTime, IN_TRADE, 2),
                Order(4, BUY, BigDecimal("210"), 6, dateTime, IN_TRADE, 4),
                Order(8, BUY, BigDecimal("210"), 4, dateTime.plusMinutes(1), IN_TRADE, 8)
            )
        )
    }
}