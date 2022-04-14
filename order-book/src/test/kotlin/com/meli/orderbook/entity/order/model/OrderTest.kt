package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.State.DONE
import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
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
        assertEquals(IN_TRADE, sellOrder.getState())
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
        assertEquals(IN_TRADE, sellOrder.getState())
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

        val size = order.getAllSizesAndCloseOrder()

        assertEquals(10, size)

        assertEquals(BigDecimal.TEN, order.price)
        assertEquals(0, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(BUY, order.type)
        assertEquals(DONE, order.getState())
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
    fun `Should let sell-order trade with an buy-order`() {
        val sellOrder = SellOrder(BigDecimal.TEN, 10, 1, dateTime, 1)
        val buyOrder = BuyOrder(BigDecimal.TEN, 5, 2, dateTime, 2)

        assertTrue(sellOrder.canTradeWith(buyOrder))
    }
}