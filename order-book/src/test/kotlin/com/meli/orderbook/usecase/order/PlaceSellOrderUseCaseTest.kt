package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.Order.State.*
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.order.service.CreateOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class PlaceSellOrderUseCaseTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var orderBookQueryGateway: OrderBookQueryGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @MockK
    lateinit var createOrderService: CreateOrderService

    @MockK
    lateinit var tradeService: TradeService

    @InjectMockKs
    lateinit var placeSellOrderUseCase: PlaceSellOrderUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should place a sell-order whith matching buy-orders in the order-book`() {

        val createSellOrderSlot = slot<SellOrder>()
        val sellOrderSlot = slot<SellOrder>()
        val buyOrderSlot = slot<List<BuyOrder>>()
        val resolvedSellOrderSlot = slot<SellOrder>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.create(capture(createSellOrderSlot))
        } returns SellOrder(BigDecimal("210"), 10, 7, dateTime, 7)
        every { tradeService.executeSell(capture(sellOrderSlot), capture(buyOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedSellOrderSlot)) } just Runs

        placeSellOrderUseCase.execute(PlaceSellOrderUseCase.Input(7, 10, BigDecimal("210")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.create(any()) }
        verify(exactly = 1) { tradeService.executeSell(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createSellOrderSlot.captured.state)
        assertEquals(BigDecimal("210"), createSellOrderSlot.captured.price)
        assertEquals(SELL, createSellOrderSlot.captured.type)
        assertEquals(7, createSellOrderSlot.captured.walletId)
        assertEquals(10, createSellOrderSlot.captured.size)

        assertEquals(IN_TRADE, sellOrderSlot.captured.state)
        assertEquals(BigDecimal("210"), sellOrderSlot.captured.price)
        assertEquals(SELL, sellOrderSlot.captured.type)
        assertEquals(7, sellOrderSlot.captured.walletId)
        assertEquals(10, sellOrderSlot.captured.size)
        assertEquals(7, sellOrderSlot.captured.id)
        assertNotNull(sellOrderSlot.captured.creationDate)

        assertEquals(IN_TRADE, buyOrderSlot.captured[0].state)
        assertEquals(BigDecimal("210"), buyOrderSlot.captured[0].price)
        assertEquals(BUY, buyOrderSlot.captured[0].type)
        assertEquals(4, buyOrderSlot.captured[0].walletId)
        assertEquals(6, buyOrderSlot.captured[0].size)
        assertEquals(4, buyOrderSlot.captured[0].id)
        assertNotNull(buyOrderSlot.captured[0].creationDate)

        assertEquals(IN_TRADE, buyOrderSlot.captured[1].state)
        assertEquals(BigDecimal("210"), buyOrderSlot.captured[1].price)
        assertEquals(BUY, buyOrderSlot.captured[1].type)
        assertEquals(8, buyOrderSlot.captured[1].walletId)
        assertEquals(4, buyOrderSlot.captured[1].size)
        assertEquals(8, buyOrderSlot.captured[1].id)
        assertNotNull(buyOrderSlot.captured[1].creationDate)

        assertEquals(IN_TRADE, resolvedSellOrderSlot.captured.state)
        assertEquals(BigDecimal("210"), resolvedSellOrderSlot.captured.price)
        assertEquals(SELL, resolvedSellOrderSlot.captured.type)
        assertEquals(7, resolvedSellOrderSlot.captured.walletId)
        assertEquals(10, resolvedSellOrderSlot.captured.size)
        assertEquals(7, resolvedSellOrderSlot.captured.id)
        assertNotNull(resolvedSellOrderSlot.captured.creationDate)

    }

    @Test
    fun `Should place a sell-order in the order-book when has no matching buy-orders`() {
        val createSellOrderSlot = slot<SellOrder>()
        val sellOrderSlot = slot<SellOrder>()
        val buyOrderSlot = slot<List<BuyOrder>>()
        val resolvedSellOrderSlot = slot<SellOrder>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.create(capture(createSellOrderSlot))
        } returns SellOrder(BigDecimal("410"), 10, 7, dateTime, 7)
        every { tradeService.executeSell(capture(sellOrderSlot), capture(buyOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedSellOrderSlot)) } just Runs

        placeSellOrderUseCase.execute(PlaceSellOrderUseCase.Input(7, 10, BigDecimal("410")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.create(any()) }
        verify(exactly = 1) { tradeService.executeSell(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createSellOrderSlot.captured.state)
        assertEquals(BigDecimal("410"), createSellOrderSlot.captured.price)
        assertEquals(SELL, createSellOrderSlot.captured.type)
        assertEquals(7, createSellOrderSlot.captured.walletId)
        assertEquals(10, createSellOrderSlot.captured.size)

        assertEquals(IN_TRADE, sellOrderSlot.captured.state)
        assertEquals(BigDecimal("410"), sellOrderSlot.captured.price)
        assertEquals(SELL, sellOrderSlot.captured.type)
        assertEquals(7, sellOrderSlot.captured.walletId)
        assertEquals(10, sellOrderSlot.captured.size)
        assertEquals(7, sellOrderSlot.captured.id)
        assertNotNull(sellOrderSlot.captured.creationDate)

        assertEquals(0, buyOrderSlot.captured.size)

        assertEquals(IN_TRADE, resolvedSellOrderSlot.captured.state)
        assertEquals(BigDecimal("410"), resolvedSellOrderSlot.captured.price)
        assertEquals(SELL, resolvedSellOrderSlot.captured.type)
        assertEquals(7, resolvedSellOrderSlot.captured.walletId)
        assertEquals(10, resolvedSellOrderSlot.captured.size)
        assertEquals(7, resolvedSellOrderSlot.captured.id)
        assertNotNull(resolvedSellOrderSlot.captured.creationDate)
    }

    private fun createOrderBook(): OrderBook {
        return OrderBook(
            listOf(
                SellOrder(BigDecimal("300"), 5, 1, dateTime, 1, IN_TRADE),
                SellOrder(BigDecimal("250"), 3, 3, dateTime, 3, IN_TRADE)
            ),
            listOf(
                BuyOrder(BigDecimal("110"), 4, 2, dateTime, 2, IN_TRADE),
                BuyOrder(BigDecimal("210"), 6, 4, dateTime, 4, IN_TRADE),
                BuyOrder(BigDecimal("210"), 4, 8, dateTime.plusMinutes(1), 8, IN_TRADE)
            )
        )
    }
}