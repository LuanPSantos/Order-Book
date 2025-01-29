package com.codeplays.orderbook.usecase.order

import com.codeplays.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.State.CREATING
import com.codeplays.orderbook.entity.order.model.Order.State.TRADING
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.order.model.OrderBook
import com.codeplays.orderbook.entity.order.service.CreatePurchaseOrderService
import com.codeplays.orderbook.entity.trade.service.TradeService
import com.codeplays.orderbook.usecase.order.PlaceOrderUseCase.Input
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class PlacePurchaseOrderUseCaseTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var orderBookQueryGateway: OrderBookQueryGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @MockK
    lateinit var createOrderService: CreatePurchaseOrderService

    @MockK
    lateinit var tradeService: TradeService

    @InjectMockKs
    lateinit var placePurchaseOrderUseCase: PlacePurchaseOrderUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should place a purchase-order whith matching sale-orders in the order-book`() {

        val createpurchaseOrderSlot = slot<Order>()
        val purchaseOrderSlot = slot<Order>()
        val saleOrderSlot = slot<List<Order>>()
        val resolvedpurchaseOrderSlot = slot<Order>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.createOrder(capture(createpurchaseOrderSlot))
        } returns Order(id = 7, walletId = 7, type = PURCHASE, price = BigDecimal("250"), size = 10, creationDate = dateTime)
        every { tradeService.execute(capture(purchaseOrderSlot), capture(saleOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedpurchaseOrderSlot)) } just Runs

        placePurchaseOrderUseCase.execute(Input(walletId = 7, size = 10, BigDecimal("250")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.createOrder(any()) }
        verify(exactly = 1) { tradeService.execute(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createpurchaseOrderSlot.captured.state)
        assertEquals(BigDecimal("250"), createpurchaseOrderSlot.captured.price)
        assertEquals(PURCHASE, createpurchaseOrderSlot.captured.type)
        assertEquals(7, createpurchaseOrderSlot.captured.walletId)
        assertEquals(10, createpurchaseOrderSlot.captured.size)

        assertEquals(TRADING, purchaseOrderSlot.captured.state)
        assertEquals(BigDecimal("250"), purchaseOrderSlot.captured.price)
        assertEquals(PURCHASE, purchaseOrderSlot.captured.type)
        assertEquals(7, purchaseOrderSlot.captured.walletId)
        assertEquals(10, purchaseOrderSlot.captured.size)
        assertEquals(7, purchaseOrderSlot.captured.id)
        Assertions.assertNotNull(purchaseOrderSlot.captured.creationDate)

        assertEquals(TRADING, saleOrderSlot.captured[0].state)
        assertEquals(BigDecimal("250"), saleOrderSlot.captured[0].price)
        assertEquals(SALE, saleOrderSlot.captured[0].type)
        assertEquals(3, saleOrderSlot.captured[0].walletId)
        assertEquals(3, saleOrderSlot.captured[0].size)
        assertEquals(3, saleOrderSlot.captured[0].id)
        Assertions.assertNotNull(saleOrderSlot.captured[0].creationDate)

        assertEquals(TRADING, resolvedpurchaseOrderSlot.captured.state)
        assertEquals(BigDecimal("250"), resolvedpurchaseOrderSlot.captured.price)
        assertEquals(PURCHASE, resolvedpurchaseOrderSlot.captured.type)
        assertEquals(7, resolvedpurchaseOrderSlot.captured.walletId)
        assertEquals(10, resolvedpurchaseOrderSlot.captured.size)
        assertEquals(7, resolvedpurchaseOrderSlot.captured.id)
        Assertions.assertNotNull(resolvedpurchaseOrderSlot.captured.creationDate)

    }

    @Test
    fun `Should place a purchase-order in the order-book when has no matching sale-orders`() {
        val createpurchaseOrderSlot = slot<Order>()
        val purchaseOrderSlot = slot<Order>()
        val saleOrderSlot = slot<List<Order>>()
        val resolvedpurchaseOrderSlot = slot<Order>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.createOrder(capture(createpurchaseOrderSlot))
        } returns Order(id = 7, walletId = 7, type = PURCHASE, price = BigDecimal("200"), size = 10, creationDate =  dateTime)
        every { tradeService.execute(capture(purchaseOrderSlot), capture(saleOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedpurchaseOrderSlot)) } just Runs

        placePurchaseOrderUseCase.execute(Input(walletId = 7, size = 10, BigDecimal("200")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.createOrder(any()) }
        verify(exactly = 1) { tradeService.execute(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createpurchaseOrderSlot.captured.state)
        assertEquals(BigDecimal("200"), createpurchaseOrderSlot.captured.price)
        assertEquals(PURCHASE, createpurchaseOrderSlot.captured.type)
        assertEquals(7, createpurchaseOrderSlot.captured.walletId)
        assertEquals(10, createpurchaseOrderSlot.captured.size)

        assertEquals(TRADING, purchaseOrderSlot.captured.state)
        assertEquals(BigDecimal("200"), purchaseOrderSlot.captured.price)
        assertEquals(PURCHASE, purchaseOrderSlot.captured.type)
        assertEquals(7, purchaseOrderSlot.captured.walletId)
        assertEquals(10, purchaseOrderSlot.captured.size)
        assertEquals(7, purchaseOrderSlot.captured.id)
        Assertions.assertNotNull(purchaseOrderSlot.captured.creationDate)

        assertEquals(0, saleOrderSlot.captured.size)

        assertEquals(TRADING, resolvedpurchaseOrderSlot.captured.state)
        assertEquals(BigDecimal("200"), resolvedpurchaseOrderSlot.captured.price)
        assertEquals(PURCHASE, resolvedpurchaseOrderSlot.captured.type)
        assertEquals(7, resolvedpurchaseOrderSlot.captured.walletId)
        assertEquals(10, resolvedpurchaseOrderSlot.captured.size)
        assertEquals(7, resolvedpurchaseOrderSlot.captured.id)
        Assertions.assertNotNull(resolvedpurchaseOrderSlot.captured.creationDate)
    }

    private fun createOrderBook(): OrderBook {
        return OrderBook(
            listOf(
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("300"), size = 5, creationDate = dateTime, state = TRADING),
                Order(id = 3, walletId = 3, type = SALE, price = BigDecimal("250"), size = 3, creationDate = dateTime, state = TRADING)
            ),
            listOf(
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("110"), size = 4, creationDate = dateTime, state = TRADING),
                Order(id = 4, walletId = 4, type = PURCHASE, price = BigDecimal("210"), size = 6, creationDate = dateTime, state = TRADING),
                Order(id = 8, walletId = 8, type = PURCHASE, price = BigDecimal("210"), size = 4, creationDate = dateTime.plusMinutes(1), state = TRADING)
            )
        )
    }
}