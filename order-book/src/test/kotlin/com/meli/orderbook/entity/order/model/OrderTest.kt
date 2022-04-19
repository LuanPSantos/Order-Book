package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.State.*
import com.meli.orderbook.entity.order.model.Order.Type.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.time.LocalDateTime.now

class OrderTest {

    private val dateTime = now()

    @Test
    fun `Should instantiate an order`() {
        val order = Order(id = 1, walletId = 1, type = SALE, price = TEN, size = 1, creationDate = dateTime, state = CREATING)

        assertEquals(TEN, order.price)
        assertEquals(1, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(SALE, order.type)
        assertEquals(CREATING, order.state)
    }

    @Test
    fun `Should not instantiate an order when size is negative`() {
        val exception = assertThrows<java.lang.IllegalArgumentException> {
            Order(walletId = 1, type = PURCHASE, price = TEN, size = -1, creationDate = dateTime)
        }

        assertEquals("Invalid order values (price or size)", exception.message)
    }

    @Test
    fun `Should not instantiate an order when price is negative`() {
        val exception = assertThrows<java.lang.IllegalArgumentException> {
            Order(walletId = 1, type = PURCHASE, price = TEN.negate(), size = 1, creationDate = dateTime)
        }

        assertEquals("Invalid order values (price or size)", exception.message)
    }

    @Test
    fun `Should retrive all sizes and close the order`() {
        val order = Order(id = 1, walletId = 1, type = PURCHASE, price = TEN, size = 10, creationDate = dateTime)

        val size = order.close().subtractAllSize()

        assertEquals(10, size)

        assertEquals(TEN, order.price)
        assertEquals(0, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(PURCHASE, order.type)
        assertEquals(CLOSED, order.state)
    }

    @Test
    fun `Should retrive all sizes and cancel the order`() {
        val order = Order(id = 1, walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime)

        val size = order.subtractAllSize()
        order.cancel()

        assertEquals(10, size)

        assertEquals(TEN, order.price)
        assertEquals(0, order.size)
        assertEquals(1, order.walletId)
        assertEquals(dateTime, order.creationDate)
        assertEquals(1, order.id)
        assertEquals(SALE, order.type)
        assertEquals(CANCELLED, order.state)
    }

    @Test
    fun `Should not let sale-order trade with an purchase-order which size is zero`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = TEN, size = 0, creationDate = dateTime)

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
    }

    @Test
    fun `Should not let sale-order trade with an cheaper purchase-order`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = ONE, size = 10, creationDate = dateTime)

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
    }

    @Test
    fun `Should not let sale-order trade with an purchase-order that is not in-trade`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime, state = TRADING)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = ONE, size = 10, creationDate = dateTime)

        purchaseOrder.close().subtractAllSize()

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
        assertEquals(0, purchaseOrder.size)
    }

    @Test
    fun `Should not let sale-order that is not in-trade trade with an purchase-order`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = ONE, size = 10, creationDate = dateTime, state = TRADING)

        saleOrder.cancel().subtractAllSize()

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
        assertEquals(0, saleOrder.size)
    }

    @Test
    fun `Should not let purchase-order trade with an sale-order which size is zero`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 0, creationDate = dateTime)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = TEN, size = 10, creationDate = dateTime)

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
    }

    @Test
    fun `Should not let purchase-order trade with a more expensive sale-order`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = ONE, size = 10, creationDate = dateTime)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = TEN, size = 10, creationDate = dateTime)

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
    }

    @Test
    fun `Should not let purchase-order trade with an sale-order that is not in-trade`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = ONE, size = 10, creationDate = dateTime, state = TRADING)

        purchaseOrder.close().subtractAllSize()

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
        assertEquals(0, purchaseOrder.size)
    }

    @Test
    fun `Should not let purchase-order that is not in-trade trade with an sale-order`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime, state = TRADING)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = ONE, size = 10, creationDate = dateTime)

        saleOrder.cancel().subtractAllSize()

        assertFalse(saleOrder.canTradeWith(purchaseOrder))
        assertEquals(0, saleOrder.size)
    }

    @Test
    fun `Should let sale-order trade with an purchase-order`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime, state = TRADING)
        val purchaseOrder = Order(walletId = 2, type = PURCHASE, price = TEN, size = 5, creationDate = dateTime, state = TRADING)

        assertTrue(saleOrder.canTradeWith(purchaseOrder))
    }

    @Test
    fun `Should make order able to trade`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime)

        saleOrder.enableToTrade()

        assertEquals(TRADING, saleOrder.state)
    }

    @Test
    fun `Should not subtract negative size value`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime, state = TRADING)

        val exception = assertThrows<java.lang.IllegalArgumentException> {
            saleOrder.subractSize(-1)
        }

        assertEquals("Subtract negative value not allowed", exception.message)
        assertEquals(TRADING, saleOrder.state)
        assertEquals(10, saleOrder.size)
    }

    @Test
    fun `Should subtract size value`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime, state = TRADING)

        saleOrder.subractSize(5)

        assertEquals(TRADING, saleOrder.state)
        assertEquals(5, saleOrder.size)
    }

    @Test
    fun `Should subtract all size value and close order`() {
        val saleOrder = Order(walletId = 1, type = SALE, price = TEN, size = 10, creationDate = dateTime, state = TRADING)

        saleOrder.subractSize(10)

        assertEquals(CLOSED, saleOrder.state)
        assertEquals(0, saleOrder.size)
    }
}