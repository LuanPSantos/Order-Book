package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.Order
import com.meli.orderbook.entity.order.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.Order.Type.BUY
import com.meli.orderbook.entity.order.Order.Type.SELL
import com.meli.orderbook.entity.order.OrderBook
import com.meli.orderbook.entity.order.gateway.OrderBookQueryGateway
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDateTime.now

class GetOrderBookUseCaseTest {

    private val dateTime = now()

    @MockK
    lateinit var orderBookQueryGateway: OrderBookQueryGateway

    @InjectMockKs
    lateinit var getOrderBookUseCase: GetOrderBookUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should get the order-book`() {

        every { orderBookQueryGateway.get() } returns createOrderBook()

        val output = getOrderBookUseCase.execute()

        assertEquals(1, output.orderBook.asks.size)
        assertEquals(1, output.orderBook.bids.size)

        assertEquals(1, output.orderBook.asks[0].id)
        assertEquals(SELL, output.orderBook.asks[0].type)
        assertEquals(BigDecimal("200"), output.orderBook.asks[0].price)
        assertEquals(dateTime, output.orderBook.asks[0].creationDate)
        assertEquals(IN_TRADE, output.orderBook.asks[0].state)
        assertEquals(1, output.orderBook.asks[0].walletId)
        assertEquals(5, output.orderBook.asks[0].size)

        assertEquals(1, output.orderBook.bids[0].id)
        assertEquals(BUY, output.orderBook.bids[0].type)
        assertEquals(BigDecimal("110"), output.orderBook.bids[0].price)
        assertEquals(dateTime, output.orderBook.bids[0].creationDate)
        assertEquals(IN_TRADE, output.orderBook.bids[0].state)
        assertEquals(2, output.orderBook.bids[0].walletId)
        assertEquals(4, output.orderBook.bids[0].size)
    }

    private fun createOrderBook(): OrderBook {
        return OrderBook(
            listOf(
                Order(1L, SELL, BigDecimal("200"), 5, dateTime, 1L)
            ),
            listOf(
                Order(1L, BUY, BigDecimal("110"), 4, dateTime, 2L)
            )
        )
    }
}