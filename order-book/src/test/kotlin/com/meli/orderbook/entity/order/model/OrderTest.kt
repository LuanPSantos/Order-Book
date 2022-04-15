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
    fun `Should instantiate an sell-order`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 1, 1, dateTime, 1)

        assertEquals(BigDecimal.TEN, sellOrder.price)
        assertEquals(1, sellOrder.size)
        assertEquals(1, sellOrder.walletId)
        assertEquals(dateTime, sellOrder.creationDate)
        assertEquals(1, sellOrder.id)
        assertEquals(SELL, sellOrder.type)
        assertEquals(CREATING, sellOrder.state)
    }

    @Test
    fun `Should instantiate an buy-order`() {
        val sellOrder = BuyOrder(BigDecimal.TEN, 1, 1, dateTime, 1)

        assertEquals(BigDecimal.TEN, sellOrder.price)
        assertEquals(1, sellOrder.size)
        assertEquals(1, sellOrder.walletId)
        assertEquals(dateTime, sellOrder.creationDate)
        assertEquals(1, sellOrder.id)
        assertEquals(BUY, sellOrder.type)
        assertEquals(CREATING, sellOrder.state)
    }

    @Test
    fun `Should not instantiate an order when size is negative`() {
        val buyOrderError = assertThrows<java.lang.IllegalArgumentException> {
            BuyOrder(BigDecimal.TEN, -1, 1, dateTime, 1)
        }

        assertEquals("Invalid order values (price or size)", buyOrderError.message)
    }

    @Test
    fun `Should not instantiate an order when price is negative`() {
        val buyOrderError = assertThrows<java.lang.IllegalArgumentException> {
            BuyOrder(BigDecimal.TEN.negate(), 1, 1, dateTime, 1)
        }

        assertEquals("Invalid order values (price or size)", buyOrderError.message)
    }

    @Test
    fun `Should retrive all sizes and close the order`() {
        val order = BuyOrder(BigDecimal.TEN, 10, 1, dateTime, 1)

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
        val order = BuyOrder(BigDecimal.TEN, 10, 1, dateTime, 1)

        val size = order.subtractAllSize()
        order.cancel()

        assertEquals(10, size)

        assertEquals(BigDecimal.TEN, order.price)
        assertEquals(0, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(BUY, order.type)
        assertEquals(CANCELLED, order.state)
    }

    @Test
    fun `Should not let sell-order trade with an buy-order which size is zero`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime, 1)
        val buyOrder = BuyOrder(BigDecimal.TEN, 0, 2, dateTime, 2)

        assertFalse(sellOrder.canTradeWith(buyOrder))
    }

    @Test
    fun `Should not let sell-order trade with an cheaper buy-order`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime, 1)
        val buyOrder = BuyOrder(BigDecimal.ONE, 10, 2, dateTime, 2)

        assertFalse(sellOrder.canTradeWith(buyOrder))
    }

    @Test
    fun `Should not let sell-order trade with an buy-order that is closed`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime, 1)
        val buyOrder = BuyOrder(BigDecimal.ONE, 10, 2, dateTime, 2)

        buyOrder.close().subtractAllSize()

        assertFalse(sellOrder.canTradeWith(buyOrder))
        assertEquals(0, buyOrder.size)
    }

    @Test
    fun `Should not let sell-order trade with an buy-order that is cancelled`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime, 1)
        val buyOrder = BuyOrder(BigDecimal.ONE, 10, 2, dateTime, 2)

        buyOrder.cancel().subtractAllSize()

        assertFalse(sellOrder.canTradeWith(buyOrder))
        assertEquals(0, buyOrder.size)
    }

    @Test
    fun `Should let sell-order trade with an buy-order`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime,  1, IN_TRADE)
        val buyOrder = BuyOrder(BigDecimal.TEN, 5, 2, dateTime,  2, IN_TRADE)

        assertTrue(sellOrder.canTradeWith(buyOrder))
    }

    @Test
    fun `Should make order able to trade`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime,  1, CREATING)

        sellOrder.enableToTrade()

        assertEquals(IN_TRADE, sellOrder.state)
    }

    @Test
    fun `Should not subtract negative size value`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime,  1, IN_TRADE)

        val exception = assertThrows<java.lang.IllegalArgumentException> {
            sellOrder.subractSizes(-1)
        }

        assertEquals("Subtract negative value not allowed", exception.message)
        assertEquals(IN_TRADE, sellOrder.state)
        assertEquals(10, sellOrder.size)
    }

    @Test
    fun `Should subtract size value`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime,  1, IN_TRADE)

        sellOrder.subractSizes(5)

        assertEquals(IN_TRADE, sellOrder.state)
        assertEquals(5, sellOrder.size)
    }

    @Test
    fun `Should subtract all size value and close order`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime,  1, IN_TRADE)

        sellOrder.subractSizes(10)

        assertEquals(CLOSED, sellOrder.state)
        assertEquals(0, sellOrder.size)
    }
}