package com.meli.orderbook.entity.order

import com.meli.orderbook.entity.order.Order
import com.meli.orderbook.entity.order.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.Order.Type.BUY
import com.meli.orderbook.entity.order.Order.Type.SELL
import com.meli.orderbook.entity.order.OrderBook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime.now

class OrderBookTest {

    private val dateTime = now()

    @Test
    fun `Should create an order-book with sell-orders and buy-orders`() {
        val orderBook = createOrderBook()

        assertEquals(4, orderBook.asks.size)
        assertEquals(1L, orderBook.asks[0].id)
        assertEquals(2L, orderBook.asks[1].id)
        assertEquals(3L, orderBook.asks[2].id)
        assertEquals(4L, orderBook.asks[3].id)

        assertEquals(4, orderBook.bids.size)
        assertEquals(1L, orderBook.bids[0].id)
        assertEquals(2L, orderBook.bids[1].id)
        assertEquals(3L, orderBook.bids[2].id)
        assertEquals(4L, orderBook.bids[3].id)
    }

    @Test
    fun `Should find matching sell-orders for a given buy-order`() {
        val orderBook = createOrderBook()
        val buyOrder = Order(5L, BUY, BigDecimal("300"), 5, now(), 5L)

        val foundOrders = orderBook.findMatchingOrders(buyOrder)

        assertEquals(2, foundOrders.size)

        assertEquals(4, foundOrders[0].id)
        assertEquals(4, foundOrders[0].size)
        assertEquals(4, foundOrders[0].walletId)
        assertEquals(BigDecimal("300"), foundOrders[0].price)
        assertEquals(IN_TRADE, foundOrders[0].state)
        assertEquals(SELL, foundOrders[0].type)
        assertEquals(dateTime.plusMinutes(1), foundOrders[0].creationDate)

        assertEquals(3, foundOrders[1].id)
        assertEquals(3, foundOrders[1].id)
        assertEquals(2, foundOrders[1].size)
        assertEquals(3, foundOrders[1].walletId)
        assertEquals(BigDecimal("300"), foundOrders[1].price)
        assertEquals(IN_TRADE, foundOrders[1].state)
        assertEquals(SELL, foundOrders[1].type)
        assertEquals(dateTime.plusMinutes(4), foundOrders[1].creationDate)
    }

    @Test
    fun `Should find matching buy-orders for a given sell-order`() {
        val orderBook = createOrderBook()
        val sellOrder = Order(5L, SELL, BigDecimal("220"), 5, now(), 5L)

        val foundOrders = orderBook.findMatchingOrders(sellOrder)

        assertEquals(2, foundOrders.size)

        assertEquals(2, foundOrders[0].id)
        assertEquals(6, foundOrders[0].size)
        assertEquals(2, foundOrders[0].walletId)
        assertEquals(BigDecimal("220"), foundOrders[0].price)
        assertEquals(IN_TRADE, foundOrders[0].state)
        assertEquals(BUY, foundOrders[0].type)
        assertEquals(dateTime.plusMinutes(2), foundOrders[0].creationDate)

        assertEquals(3, foundOrders[1].id)
        assertEquals(3, foundOrders[1].id)
        assertEquals(7, foundOrders[1].size)
        assertEquals(3, foundOrders[1].walletId)
        assertEquals(BigDecimal("220"), foundOrders[1].price)
        assertEquals(IN_TRADE, foundOrders[1].state)
        assertEquals(BUY, foundOrders[1].type)
        assertEquals(dateTime.plusMinutes(3), foundOrders[1].creationDate)
    }

    private fun createOrderBook(): OrderBook {

        return OrderBook(
            listOf(
                Order(1L, SELL, BigDecimal("200"), 5, dateTime.plusMinutes(3), 1L),
                Order(2L, SELL, BigDecimal("100"), 1, dateTime.plusMinutes(2), 2L),
                Order(3L, SELL, BigDecimal("300"), 2, dateTime.plusMinutes(4), 3L),
                Order(4L, SELL, BigDecimal("300"), 4, dateTime.plusMinutes(1), 4L)
            ),
            listOf(
                Order(1L, BUY, BigDecimal("110"), 4, dateTime.plusMinutes(4), 1L),
                Order(2L, BUY, BigDecimal("220"), 6, dateTime.plusMinutes(2), 2L),
                Order(3L, BUY, BigDecimal("220"), 7, dateTime.plusMinutes(3), 3L),
                Order(4L, BUY, BigDecimal("355"), 2, dateTime.plusMinutes(1), 4L)
            )
        )
    }
}