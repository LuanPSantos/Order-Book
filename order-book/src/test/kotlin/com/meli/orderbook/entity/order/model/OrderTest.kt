package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.State.*
import com.meli.orderbook.entity.order.model.Order.Type.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime.now

class OrderTest {

    private val dateTime = now()

    @Test
    fun `Should instantiate an order`() {
        val order = Order(1, SELL, BigDecimal.TEN, 1, dateTime, CREATING,1)

        assertEquals(BigDecimal.TEN, order.price)
        assertEquals(1, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(SELL, order.type)
        assertEquals(CREATING, order.state)
    }

    @Test
    fun `Should not instantiate an order when size is negative`() {
        val exception = assertThrows<java.lang.IllegalArgumentException> {
            Order(1, BUY, BigDecimal.TEN, -1, dateTime)
        }

        assertEquals("Invalid order values (price or size)", exception.message)
    }

    @Test
    fun `Should not instantiate an order when price is negative`() {
        val exception = assertThrows<java.lang.IllegalArgumentException> {
            Order(1, BUY, BigDecimal.TEN.negate(), 1, dateTime)
        }

        assertEquals("Invalid order values (price or size)", exception.message)
    }

    @Test
    fun `Should retrive all sizes and close the order`() {
        val order = Order(1, BUY,BigDecimal.TEN, 10, dateTime, id = 1)

        val size = order.close().subtractAllSize()

        assertEquals(10, size)

        assertEquals(BigDecimal.TEN, order.price)
        assertEquals(0, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(BUY, order.type)
        assertEquals(CLOSED, order.state)
    }

    @Test
    fun `Should retrive all sizes and cancel the order`() {
        val order = Order(1, SELL, BigDecimal.TEN, 10, dateTime, id = 1)

        val size = order.subtractAllSize()
        order.cancel()

        assertEquals(10, size)

        assertEquals(BigDecimal.TEN, order.price)
        assertEquals(0, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(SELL, order.type)
        assertEquals(CANCELLED, order.state)
    }

    @Test
    fun `Should not let sell-order trade with an buy-order which size is zero`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime)
        val buyOrder = Order(2, BUY, BigDecimal.TEN, 0, dateTime)

        assertFalse(sellOrder.canTradeWith(buyOrder))
    }

    @Test
    fun `Should not let sell-order trade with an cheaper buy-order`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime)
        val buyOrder = Order(2, BUY, BigDecimal.ONE, 10, dateTime)

        assertFalse(sellOrder.canTradeWith(buyOrder))
    }

    @Test
    fun `Should not let sell-order trade with an buy-order that is closed`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime)
        val buyOrder = Order(2, BUY, BigDecimal.ONE, 10, dateTime)

        buyOrder.close().subtractAllSize()

        assertFalse(sellOrder.canTradeWith(buyOrder))
        assertEquals(0, buyOrder.size)
    }

    @Test
    fun `Should not let sell-order trade with an buy-order that is cancelled`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime)
        val buyOrder = Order(2, BUY, BigDecimal.ONE, 10, dateTime)

        buyOrder.cancel().subtractAllSize()

        assertFalse(sellOrder.canTradeWith(buyOrder))
        assertEquals(0, buyOrder.size)
    }

    @Test
    fun `Should let sell-order trade with an buy-order`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime, IN_TRADE)
        val buyOrder = Order(2, BUY, BigDecimal.TEN, 5, dateTime, IN_TRADE)

        assertTrue(sellOrder.canTradeWith(buyOrder))
    }

    @Test
    fun `Should make order able to trade`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime)

        sellOrder.enableToTrade()

        assertEquals(IN_TRADE, sellOrder.state)
    }

    @Test
    fun `Should not subtract negative size value`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime, IN_TRADE)

        val exception = assertThrows<java.lang.IllegalArgumentException> {
            sellOrder.subractSize(-1)
        }

        assertEquals("Subtract negative value not allowed", exception.message)
        assertEquals(IN_TRADE, sellOrder.state)
        assertEquals(10, sellOrder.size)
    }

    @Test
    fun `Should subtract size value`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime, IN_TRADE)

        sellOrder.subractSize(5)

        assertEquals(IN_TRADE, sellOrder.state)
        assertEquals(5, sellOrder.size)
    }

    @Test
    fun `Should subtract all size value and close order`() {
        val sellOrder = Order(1, SELL, BigDecimal.TEN, 10, dateTime, IN_TRADE)

        sellOrder.subractSize(10)

        assertEquals(CLOSED, sellOrder.state)
        assertEquals(0, sellOrder.size)
    }
}