package com.codeplays.orderbook.usecase.order

import com.codeplays.orderbook.entity.order.model.Order.State.TRADING
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.order.model.OrderBook
import com.codeplays.orderbook.entity.order.gateway.OrderBookQueryGateway
import com.codeplays.orderbook.entity.order.model.Order
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
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
        assertEquals(SALE, output.orderBook.asks[0].type)
        assertEquals(BigDecimal("200"), output.orderBook.asks[0].price)
        assertEquals(dateTime, output.orderBook.asks[0].creationDate)
        assertEquals(TRADING, output.orderBook.asks[0].state)
        assertEquals(1, output.orderBook.asks[0].walletId)
        assertEquals(5, output.orderBook.asks[0].size)

        assertEquals(2, output.orderBook.bids[0].id)
        assertEquals(PURCHASE, output.orderBook.bids[0].type)
        assertEquals(BigDecimal("110"), output.orderBook.bids[0].price)
        assertEquals(dateTime, output.orderBook.bids[0].creationDate)
        assertEquals(TRADING, output.orderBook.bids[0].state)
        assertEquals(2, output.orderBook.bids[0].walletId)
        assertEquals(4, output.orderBook.bids[0].size)

        verify(exactly = 1) { orderBookQueryGateway.get() }
    }

    private fun createOrderBook(): OrderBook {
        return OrderBook(
            listOf(
                Order(id = 1, walletId = 1, type =  SALE, price = BigDecimal("200"), size = 5, creationDate = dateTime, state = TRADING)
            ),
            listOf(
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("110"), size = 4, creationDate = dateTime, state = TRADING)
            )
        )
    }
}