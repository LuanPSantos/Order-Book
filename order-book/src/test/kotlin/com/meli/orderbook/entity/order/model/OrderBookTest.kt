package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.State.TRADING
import com.meli.orderbook.entity.order.model.Order.Type.PURCHASE
import com.meli.orderbook.entity.order.model.Order.Type.SALE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime.now

class OrderBookTest {

    private val dateTime = now()

    @Test
    fun `Should create an order-book with sale-orders and purchase-orders`() {
        val orderBook = createOrderBook()

        assertEquals(4, orderBook.asks.size)
        assertEquals(2L, orderBook.asks[0].id)
        assertEquals(1L, orderBook.asks[1].id)
        assertEquals(4L, orderBook.asks[2].id)
        assertEquals(3L, orderBook.asks[3].id)

        assertEquals(4, orderBook.bids.size)
        assertEquals(4L, orderBook.bids[0].id)
        assertEquals(2L, orderBook.bids[1].id)
        assertEquals(3L, orderBook.bids[2].id)
        assertEquals(1L, orderBook.bids[3].id)
    }

    @Test
    fun `Should find matching sale-orders for a given purchase-order`() {
        val orderBook = createOrderBook()
        val purchaseOrder = Order(
            walletId = 5,
            type = PURCHASE,
            price = BigDecimal("200"),
            size = 5,
            creationDate = now(),
            state = TRADING
        )

        val matchingSaleOrders = orderBook.findMatchingOrders(purchaseOrder)

        assertEquals(2, matchingSaleOrders.size)

        assertEquals(2, matchingSaleOrders[0].id)
        assertEquals(1, matchingSaleOrders[0].size)
        assertEquals(2, matchingSaleOrders[0].walletId)
        assertEquals(BigDecimal("100"), matchingSaleOrders[0].price)
        assertEquals(TRADING, matchingSaleOrders[0].state)
        assertEquals(SALE, matchingSaleOrders[0].type)
        assertEquals(dateTime.plusMinutes(2), matchingSaleOrders[0].creationDate)

        assertEquals(1, matchingSaleOrders[1].id)
        assertEquals(5, matchingSaleOrders[1].size)
        assertEquals(1, matchingSaleOrders[1].walletId)
        assertEquals(BigDecimal("200"), matchingSaleOrders[1].price)
        assertEquals(TRADING, matchingSaleOrders[1].state)
        assertEquals(SALE, matchingSaleOrders[1].type)
        assertEquals(dateTime.plusMinutes(3), matchingSaleOrders[1].creationDate)
    }

    @Test
    fun `Should find matching purchase-orders for a given sale-order`() {
        val orderBook = createOrderBook()
        val saleOrder = Order(
            walletId = 5,
            type = SALE,
            price = BigDecimal("220"),
            size = 5,
            creationDate = now(),
            state = TRADING
        )

        val matchingPurchaseOrders = orderBook.findMatchingOrders(saleOrder)

        assertEquals(3, matchingPurchaseOrders.size)

        assertEquals(4, matchingPurchaseOrders[0].id)
        assertEquals(2, matchingPurchaseOrders[0].size)
        assertEquals(4, matchingPurchaseOrders[0].walletId)
        assertEquals(BigDecimal("355"), matchingPurchaseOrders[0].price)
        assertEquals(TRADING, matchingPurchaseOrders[0].state)
        assertEquals(PURCHASE, matchingPurchaseOrders[0].type)
        assertEquals(dateTime.plusMinutes(1), matchingPurchaseOrders[0].creationDate)

        assertEquals(2, matchingPurchaseOrders[1].id)
        assertEquals(6, matchingPurchaseOrders[1].size)
        assertEquals(2, matchingPurchaseOrders[1].walletId)
        assertEquals(BigDecimal("220"), matchingPurchaseOrders[1].price)
        assertEquals(TRADING, matchingPurchaseOrders[1].state)
        assertEquals(PURCHASE, matchingPurchaseOrders[1].type)
        assertEquals(dateTime.plusMinutes(2), matchingPurchaseOrders[1].creationDate)

        assertEquals(3, matchingPurchaseOrders[2].id)
        assertEquals(3, matchingPurchaseOrders[2].id)
        assertEquals(7, matchingPurchaseOrders[2].size)
        assertEquals(3, matchingPurchaseOrders[2].walletId)
        assertEquals(BigDecimal("220"), matchingPurchaseOrders[2].price)
        assertEquals(TRADING, matchingPurchaseOrders[2].state)
        assertEquals(PURCHASE, matchingPurchaseOrders[2].type)
        assertEquals(dateTime.plusMinutes(3), matchingPurchaseOrders[2].creationDate)
    }

    private fun createOrderBook(): OrderBook {

        return OrderBook(
            listOf(
                Order(
                    id = 1,
                    walletId = 1,
                    type = SALE,
                    price = BigDecimal("200"),
                    size = 5,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(3)
                ),
                Order(
                    id = 2,
                    walletId = 2,
                    type = SALE,
                    price = BigDecimal("100"),
                    size = 1,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(2)
                ),
                Order(
                    id = 3,
                    walletId = 3,
                    type = SALE,
                    price = BigDecimal("300"),
                    size = 2,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(4)
                ),
                Order(
                    id = 4,
                    walletId = 4,
                    type = SALE,
                    price = BigDecimal("300"),
                    size = 4,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(1)
                )
            ),
            listOf(
                Order(
                    id = 1,
                    walletId = 1,
                    type = PURCHASE,
                    price = BigDecimal("110"),
                    size = 4,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(4)
                ),
                Order(
                    id = 2,
                    walletId = 2,
                    type = PURCHASE,
                    price = BigDecimal("220"),
                    size = 6,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(2)
                ),
                Order(
                    id = 3,
                    walletId = 3,
                    type = PURCHASE,
                    price = BigDecimal("220"),
                    size = 7,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(3)
                ),
                Order(
                    id = 4,
                    walletId = 4,
                    type = PURCHASE,
                    price = BigDecimal("355"),
                    size = 2,
                    state = TRADING,
                    creationDate = dateTime.plusMinutes(1)
                )
            )
        )
    }
}