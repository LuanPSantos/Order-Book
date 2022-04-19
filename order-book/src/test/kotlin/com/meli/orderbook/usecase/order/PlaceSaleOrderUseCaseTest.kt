package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.*
import com.meli.orderbook.entity.order.model.Order.Type.PURCHASE
import com.meli.orderbook.entity.order.model.Order.Type.SALE
import com.meli.orderbook.entity.order.model.OrderBook
import com.meli.orderbook.entity.order.service.CreateSaleOrderService
import com.meli.orderbook.entity.trade.service.TradeService
import com.meli.orderbook.usecase.order.PlaceOrderUseCase.Input
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class PlaceSaleOrderUseCaseTest {

    private val dateTime = LocalDateTime.now()

    @MockK
    lateinit var orderBookQueryGateway: OrderBookQueryGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @MockK
    lateinit var createOrderService: CreateSaleOrderService

    @MockK
    lateinit var tradeService: TradeService

    @InjectMockKs
    lateinit var placeSaleOrderUseCase: PlaceSaleOrderUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should place a sale-order whith matching purchase-orders in the order-book`() {

        val createsaleOrderSlot = slot<Order>()
        val saleOrderSlot = slot<Order>()
        val purchaseOrderSlot = slot<List<Order>>()
        val resolvedsaleOrderSlot = slot<Order>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.createOrder(capture(createsaleOrderSlot))
        } returns Order(id = 7, walletId = 7, type = SALE, price = BigDecimal("210"), size = 10, creationDate = dateTime)
        every { tradeService.execute(capture(saleOrderSlot), capture(purchaseOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedsaleOrderSlot)) } just Runs

        placeSaleOrderUseCase.execute(Input(walletId = 7, size = 10, price = BigDecimal("210")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.createOrder(any()) }
        verify(exactly = 1) { tradeService.execute(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createsaleOrderSlot.captured.state)
        assertEquals(BigDecimal("210"), createsaleOrderSlot.captured.price)
        assertEquals(SALE, createsaleOrderSlot.captured.type)
        assertEquals(7, createsaleOrderSlot.captured.walletId)
        assertEquals(10, createsaleOrderSlot.captured.size)

        assertEquals(TRADING, saleOrderSlot.captured.state)
        assertEquals(BigDecimal("210"), saleOrderSlot.captured.price)
        assertEquals(SALE, saleOrderSlot.captured.type)
        assertEquals(7, saleOrderSlot.captured.walletId)
        assertEquals(10, saleOrderSlot.captured.size)
        assertEquals(7, saleOrderSlot.captured.id)
        assertNotNull(saleOrderSlot.captured.creationDate)

        assertEquals(TRADING, purchaseOrderSlot.captured[0].state)
        assertEquals(BigDecimal("210"), purchaseOrderSlot.captured[0].price)
        assertEquals(PURCHASE, purchaseOrderSlot.captured[0].type)
        assertEquals(4, purchaseOrderSlot.captured[0].walletId)
        assertEquals(6, purchaseOrderSlot.captured[0].size)
        assertEquals(4, purchaseOrderSlot.captured[0].id)
        assertNotNull(purchaseOrderSlot.captured[0].creationDate)

        assertEquals(TRADING, purchaseOrderSlot.captured[1].state)
        assertEquals(BigDecimal("210"), purchaseOrderSlot.captured[1].price)
        assertEquals(PURCHASE, purchaseOrderSlot.captured[1].type)
        assertEquals(8, purchaseOrderSlot.captured[1].walletId)
        assertEquals(4, purchaseOrderSlot.captured[1].size)
        assertEquals(8, purchaseOrderSlot.captured[1].id)
        assertNotNull(purchaseOrderSlot.captured[1].creationDate)

        assertEquals(TRADING, resolvedsaleOrderSlot.captured.state)
        assertEquals(BigDecimal("210"), resolvedsaleOrderSlot.captured.price)
        assertEquals(SALE, resolvedsaleOrderSlot.captured.type)
        assertEquals(7, resolvedsaleOrderSlot.captured.walletId)
        assertEquals(10, resolvedsaleOrderSlot.captured.size)
        assertEquals(7, resolvedsaleOrderSlot.captured.id)
        assertNotNull(resolvedsaleOrderSlot.captured.creationDate)

    }

    @Test
    fun `Should place a sale-order in the order-book when has no matching purchase-orders`() {
        val createsaleOrderSlot = slot<Order>()
        val saleOrderSlot = slot<Order>()
        val purchaseOrderSlot = slot<List<Order>>()
        val resolvedsaleOrderSlot = slot<Order>()

        every { orderBookQueryGateway.get() } returns createOrderBook()
        every {
            createOrderService.createOrder(capture(createsaleOrderSlot))
        } returns Order(id = 7, walletId = 7, type = SALE, price = BigDecimal("410"), size = 10, creationDate = dateTime)
        every { tradeService.execute(capture(saleOrderSlot), capture(purchaseOrderSlot)) } just Runs
        every { orderCommandGateway.update(capture(resolvedsaleOrderSlot)) } just Runs

        placeSaleOrderUseCase.execute(Input(walletId = 7, size = 10, price = BigDecimal("410")))

        verify(exactly = 1) { orderBookQueryGateway.get() }
        verify(exactly = 1) { createOrderService.createOrder(any()) }
        verify(exactly = 1) { tradeService.execute(any(), any()) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CREATING, createsaleOrderSlot.captured.state)
        assertEquals(BigDecimal("410"), createsaleOrderSlot.captured.price)
        assertEquals(SALE, createsaleOrderSlot.captured.type)
        assertEquals(7, createsaleOrderSlot.captured.walletId)
        assertEquals(10, createsaleOrderSlot.captured.size)

        assertEquals(TRADING, saleOrderSlot.captured.state)
        assertEquals(BigDecimal("410"), saleOrderSlot.captured.price)
        assertEquals(SALE, saleOrderSlot.captured.type)
        assertEquals(7, saleOrderSlot.captured.walletId)
        assertEquals(10, saleOrderSlot.captured.size)
        assertEquals(7, saleOrderSlot.captured.id)
        assertNotNull(saleOrderSlot.captured.creationDate)

        assertEquals(0, purchaseOrderSlot.captured.size)

        assertEquals(TRADING, resolvedsaleOrderSlot.captured.state)
        assertEquals(BigDecimal("410"), resolvedsaleOrderSlot.captured.price)
        assertEquals(SALE, resolvedsaleOrderSlot.captured.type)
        assertEquals(7, resolvedsaleOrderSlot.captured.walletId)
        assertEquals(10, resolvedsaleOrderSlot.captured.size)
        assertEquals(7, resolvedsaleOrderSlot.captured.id)
        assertNotNull(resolvedsaleOrderSlot.captured.creationDate)
    }

    private fun createOrderBook(): OrderBook {
        return OrderBook(
            listOf(
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("300"), size = 5, creationDate = dateTime, state = TRADING),
                Order(id = 3, walletId = 3, type = SALE, price = BigDecimal("250"), size = 3,  creationDate = dateTime, state = TRADING)
            ),
            listOf(
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("110"), size = 4, creationDate = dateTime, state = TRADING),
                Order(id = 4, walletId = 4, type = PURCHASE, price = BigDecimal("210"), size = 6, creationDate = dateTime, state = TRADING),
                Order(id = 8, walletId = 8, type = PURCHASE, price = BigDecimal("210"), size = 4, creationDate = dateTime.plusMinutes(1), state = TRADING)
            )
        )
    }
}