package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.CLOSED
import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.trade.gateway.TradeHistoryCommandGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.stream.Stream


class TradeServiceTest {

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @MockK
    lateinit var walletCommandGateway: WalletCommandGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @MockK
    lateinit var tradeHistoryCommandGateway: TradeHistoryCommandGateway

    @InjectMockKs
    lateinit var tradeService: TradeService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should not execute a trande when sell price is greater than buy price`() {

        tradeService.executeBuy(
            BuyOrder(BigDecimal("10"), 10, 2, dateTime, 2, IN_TRADE),
            listOf(SellOrder(BigDecimal("15"), 10, 1, dateTime, 1, IN_TRADE))
        )

        verify(exactly = 0) { walletQueryGateway.findById(any()) }
        verify(exactly = 0) { walletQueryGateway.findById(any()) }
        verify(exactly = 0) { walletCommandGateway.update(any()) }
        verify(exactly = 0) { orderCommandGateway.update(any()) }
        verify(exactly = 0) { tradeHistoryCommandGateway.register(any()) }
    }

    @ParameterizedTest
    @MethodSource("testScenarios")
    fun `Should execute a trande`(
        sellerWallet: Wallet,
        buyerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder,
        expectedSellerWallet: Wallet,
        expectedBuyerWallet: Wallet,
        expectedSellOrder: SellOrder,
        expectedBuyOrder: BuyOrder,
        expectedTrade: Trade
    ) {
        val walletsSlot = mutableListOf<Wallet>()
        val ordersSlot = mutableListOf<Order>()
        val tradeSlot = slot<Trade>()

        every { walletQueryGateway.findById(eq(sellerWallet.id)) } returns sellerWallet
        every { walletQueryGateway.findById(eq(buyerWallet.id)) } returns buyerWallet
        every { walletCommandGateway.update(capture(walletsSlot)) } just Runs
        every { orderCommandGateway.update(capture(ordersSlot)) } just Runs
        every { tradeHistoryCommandGateway.register(capture(tradeSlot)) } just Runs

        tradeService.executeSell(
            sellOrder,
            listOf(buyOrder)
        )

        verify(exactly = 1) { walletQueryGateway.findById(eq(sellerWallet.id)) }
        verify(exactly = 1) { walletQueryGateway.findById(eq(buyerWallet.id)) }
        verify(exactly = 2) { walletCommandGateway.update(any()) }
        verify(exactly = 2) { orderCommandGateway.update(any()) }
        verify(exactly = 1) { tradeHistoryCommandGateway.register(any()) }

        val sellerWalletCaptured = walletsSlot.find { it.id == sellerWallet.id }
        assertEquals(expectedSellerWallet.id, sellerWalletCaptured?.id)
        assertEquals(expectedSellerWallet.amountOfAssets, sellerWalletCaptured?.amountOfAssets)
        assertEquals(expectedSellerWallet.amountOfMoney, sellerWalletCaptured?.amountOfMoney)

        val buyerWalletCaptured = walletsSlot.find { it.id == buyerWallet.id }
        assertEquals(expectedBuyerWallet.id, buyerWalletCaptured?.id)
        assertEquals(expectedBuyerWallet.amountOfAssets, buyerWalletCaptured?.amountOfAssets)
        assertEquals(expectedBuyerWallet.amountOfMoney, buyerWalletCaptured?.amountOfMoney)

        val sellOrderCaptured = ordersSlot.find { it.id == sellOrder.id }
        assertEquals(expectedSellOrder.id, sellOrderCaptured?.id)
        assertEquals(expectedSellOrder.walletId, sellOrderCaptured?.walletId)
        assertEquals(expectedSellOrder.size, sellOrderCaptured?.size)
        assertEquals(expectedSellOrder.creationDate, sellOrderCaptured?.creationDate)
        assertEquals(expectedSellOrder.price, sellOrderCaptured?.price)
        assertEquals(expectedSellOrder.type, sellOrderCaptured?.type)
        assertEquals(expectedSellOrder.state, sellOrderCaptured?.state)

        val buyOrderCaptured = ordersSlot.find { it.id == buyOrder.id }
        assertEquals(expectedBuyOrder.id, buyOrderCaptured?.id)
        assertEquals(expectedBuyOrder.walletId, buyOrderCaptured?.walletId)
        assertEquals(expectedBuyOrder.size, buyOrderCaptured?.size)
        assertEquals(expectedBuyOrder.creationDate, buyOrderCaptured?.creationDate)
        assertEquals(expectedBuyOrder.price, buyOrderCaptured?.price)
        assertEquals(expectedBuyOrder.type, buyOrderCaptured?.type)
        assertEquals(expectedBuyOrder.state, buyOrderCaptured?.state)

        assertNull(tradeSlot.captured.id)
        assertEquals(expectedTrade.size, tradeSlot.captured.size)
        assertEquals(expectedTrade.price, tradeSlot.captured.price)
        assertEquals(expectedTrade.sellOrderId, tradeSlot.captured.sellOrderId)
        assertEquals(expectedTrade.buyerOrderId, tradeSlot.captured.buyerOrderId)
        assertEquals(expectedTrade.type, tradeSlot.captured.type)
        assertNotNull(tradeSlot.captured.creationDate)
    }

    private companion object {
        val dateTime: LocalDateTime = now()

        @JvmStatic
        fun testScenarios(): Stream<Arguments> {
            return Stream.of(
                pricesMatchedAndSizeMetched(),
                pricesMatchedAndMoreSellThanBuy(),
                pricesMatchedAndLessSellThanBuy(),
                sizeMatchedAndSellCheaper(),
                moreSellThanBuyAndSellCheaper(),
                lessSellThanBuyAndSellCheaper()
            )
        }

        private fun pricesMatchedAndSizeMetched(): Arguments {
            return arguments(
                Wallet(1, BigDecimal("10"), 10),
                Wallet(2, BigDecimal("10"), 10),
                SellOrder(BigDecimal("10"), 10, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 10, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("110"), 10),
                Wallet(2, BigDecimal("10"), 20),
                SellOrder(BigDecimal("10"), 0, 1, dateTime, 1, CLOSED),
                BuyOrder(BigDecimal("10"), 0, 2, dateTime, 2, CLOSED),
                Trade(1, 2, Order.Type.SELL, 10, BigDecimal("100"), BigDecimal("0"), dateTime)
            )
        }

        private fun pricesMatchedAndMoreSellThanBuy(): Arguments {
            return arguments(
                Wallet(1, BigDecimal("10"), 10),
                Wallet(2, BigDecimal("10"), 10),
                SellOrder(BigDecimal("10"), 15, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 10, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("110"), 10),
                Wallet(2, BigDecimal("10"), 20),
                SellOrder(BigDecimal("10"), 5, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 0, 2, dateTime, 2, CLOSED),
                Trade(1, 2, Order.Type.SELL, 10, BigDecimal("100"), BigDecimal("0"), dateTime)
            )
        }

        private fun pricesMatchedAndLessSellThanBuy(): Arguments {
            return arguments(
                Wallet(1, BigDecimal("10"), 10),
                Wallet(2, BigDecimal("10"), 10),
                SellOrder(BigDecimal("10"), 10, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 15, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("110"), 10),
                Wallet(2, BigDecimal("10"), 20),
                SellOrder(BigDecimal("10"), 0, 1, dateTime, 1, CLOSED),
                BuyOrder(BigDecimal("10"), 5, 2, dateTime, 2, IN_TRADE),
                Trade(1, 2, Order.Type.SELL, 10, BigDecimal("100"), BigDecimal("0"), dateTime)
            )
        }

        private fun sizeMatchedAndSellCheaper(): Arguments {
            return arguments(
                Wallet(1, BigDecimal("10"), 10),
                Wallet(2, BigDecimal("10"), 10),
                SellOrder(BigDecimal("9"), 10, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 10, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("100"), 10),
                Wallet(2, BigDecimal("20"), 20),
                SellOrder(BigDecimal("9"), 0, 1, dateTime, 1, CLOSED),
                BuyOrder(BigDecimal("10"), 0, 2, dateTime, 2, CLOSED),
                Trade(1, 2, Order.Type.SELL, 10, BigDecimal("90"), BigDecimal("10"), dateTime)
            )
        }

        private fun moreSellThanBuyAndSellCheaper(): Arguments {
            return arguments(
                Wallet(1, BigDecimal("10"), 10),
                Wallet(2, BigDecimal("10"), 10),
                SellOrder(BigDecimal("9"), 15, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 10, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("100"), 10),
                Wallet(2, BigDecimal("20"), 20),
                SellOrder(BigDecimal("9"), 5, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 0, 2, dateTime, 2, CLOSED),
                Trade(1, 2, Order.Type.SELL, 10, BigDecimal("90"), BigDecimal("10"), dateTime)
            )
        }

        private fun lessSellThanBuyAndSellCheaper(): Arguments {
            return arguments(
                Wallet(1, BigDecimal("10"), 10),
                Wallet(2, BigDecimal("10"), 10),
                SellOrder(BigDecimal("9"), 10, 1, dateTime, 1, IN_TRADE),
                BuyOrder(BigDecimal("10"), 15, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("100"), 10),
                Wallet(2, BigDecimal("20"), 20),
                SellOrder(BigDecimal("9"), 0, 1, dateTime, 1, CLOSED),
                BuyOrder(BigDecimal("10"), 5, 2, dateTime, 2, IN_TRADE),
                Trade(1, 2, Order.Type.SELL, 10, BigDecimal("90"), BigDecimal("10"), dateTime)
            )
        }
    }
}