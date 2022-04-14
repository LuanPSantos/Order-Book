package com.meli.orderbook.entity.order.model

import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
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
    fun `Should find matching sell-orders for a given buy-order`() {
        val orderBook = createOrderBook()
        val buyOrder = BuyOrder(BigDecimal("200"), 5, 5, now())

        val matchingSellOrders = orderBook.findMatchingSellsOrder(buyOrder)

        assertEquals(2, matchingSellOrders.size)

        assertEquals(2, matchingSellOrders[0].id)
        assertEquals(1, matchingSellOrders[0].size)
        assertEquals(2, matchingSellOrders[0].walletId)
        assertEquals(BigDecimal("100"), matchingSellOrders[0].price)
        assertEquals(IN_TRADE, matchingSellOrders[0].getState())
        assertEquals(SELL, matchingSellOrders[0].type)
        assertEquals(dateTime.plusMinutes(2), matchingSellOrders[0].creationDate)

        assertEquals(1, matchingSellOrders[1].id)
        assertEquals(5, matchingSellOrders[1].size)
        assertEquals(1, matchingSellOrders[1].walletId)
        assertEquals(BigDecimal("200"), matchingSellOrders[1].price)
        assertEquals(IN_TRADE, matchingSellOrders[1].getState())
        assertEquals(SELL, matchingSellOrders[1].type)
        assertEquals(dateTime.plusMinutes(3), matchingSellOrders[1].creationDate)
    }

    @Test
    fun `Should find matching buy-orders for a given sell-order`() {
        val orderBook = createOrderBook()
        val sellOrder = SellOrder(BigDecimal("220"), 5, 5L, now())

        val matchingBuyOrders = orderBook.findMatchingBuyOrders(sellOrder)

        assertEquals(3, matchingBuyOrders.size)

        assertEquals(4, matchingBuyOrders[0].id)
        assertEquals(2, matchingBuyOrders[0].size)
        assertEquals(4, matchingBuyOrders[0].walletId)
        assertEquals(BigDecimal("355"), matchingBuyOrders[0].price)
        assertEquals(IN_TRADE, matchingBuyOrders[0].getState())
        assertEquals(BUY, matchingBuyOrders[0].type)
        assertEquals(dateTime.plusMinutes(1), matchingBuyOrders[0].creationDate)

        assertEquals(2, matchingBuyOrders[1].id)
        assertEquals(6, matchingBuyOrders[1].size)
        assertEquals(2, matchingBuyOrders[1].walletId)
        assertEquals(BigDecimal("220"), matchingBuyOrders[1].price)
        assertEquals(IN_TRADE, matchingBuyOrders[1].getState())
        assertEquals(BUY, matchingBuyOrders[1].type)
        assertEquals(dateTime.plusMinutes(2), matchingBuyOrders[1].creationDate)

        assertEquals(3, matchingBuyOrders[2].id)
        assertEquals(3, matchingBuyOrders[2].id)
        assertEquals(7, matchingBuyOrders[2].size)
        assertEquals(3, matchingBuyOrders[2].walletId)
        assertEquals(BigDecimal("220"), matchingBuyOrders[2].price)
        assertEquals(IN_TRADE, matchingBuyOrders[2].getState())
        assertEquals(BUY, matchingBuyOrders[2].type)
        assertEquals(dateTime.plusMinutes(3), matchingBuyOrders[2].creationDate)
    }

    private fun createOrderBook(): OrderBook {

        return OrderBook(
            listOf(
                SellOrder(BigDecimal("200"), 5, 1, dateTime.plusMinutes(3), 1),
                SellOrder(BigDecimal("100"), 1, 2, dateTime.plusMinutes(2), 2),
                SellOrder(BigDecimal("300"), 2, 3, dateTime.plusMinutes(4), 3),
                SellOrder(BigDecimal("300"), 4, 4, dateTime.plusMinutes(1), 4)
            ),
            listOf(
                BuyOrder(BigDecimal("110"), 4, 1L, dateTime.plusMinutes(4), 1),
                BuyOrder(BigDecimal("220"), 6, 2L, dateTime.plusMinutes(2), 2),
                BuyOrder(BigDecimal("220"), 7, 3L, dateTime.plusMinutes(3), 3),
                BuyOrder(BigDecimal("355"), 2, 4L, dateTime.plusMinutes(1), 4)
            )
        )
    }
}