package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.BuyOrder
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.CLOSED
import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.model.Order.Type.BUY
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.order.model.SellOrder
import com.meli.orderbook.entity.trade.gateway.TradeHistoricCommandGateway
import com.meli.orderbook.entity.trade.model.Trade
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.math.BigDecimal.TEN
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
    lateinit var tradeHistoricCommandGateway: TradeHistoricCommandGateway

    @InjectMockKs
    lateinit var tradeService: TradeService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @ParameterizedTest
    @MethodSource("testScenarios")
    fun `Should execute a sell trande`(
        sellerWallet: Wallet,
        buyerWallet: Wallet,
        sellOrder: SellOrder,
        buyOrder: BuyOrder,
        expectedSellerWallet: Wallet,
        expectedBuyerWallet: Wallet,
        expectedSellOrder: SellOrder,
        expectedBuyOrder: BuyOrder
    ) {
        val walletsSlot = mutableListOf<Wallet>()
        val ordersSlot = mutableListOf<Order>()

        val tradeSlot = slot<Trade>()

        every { walletQueryGateway.findById(eq(sellerWallet.id)) } returns sellerWallet
        every { walletQueryGateway.findById(eq(buyerWallet.id)) } returns buyerWallet
        every { walletCommandGateway.update(capture(walletsSlot)) } just Runs
        every { orderCommandGateway.update(capture(ordersSlot)) } just Runs
        every { tradeHistoricCommandGateway.register(capture(tradeSlot)) } just Runs

        tradeService.executeSell(
            sellOrder,
            buyOrder
        )

        verify(exactly = 1) { walletQueryGateway.findById(eq(sellerWallet.id)) }
        verify(exactly = 1) { walletQueryGateway.findById(eq(buyerWallet.id)) }
        verify(exactly = 2) { walletCommandGateway.update(any()) }
        verify(exactly = 2) { orderCommandGateway.update(any()) }
        verify(exactly = 1) { tradeHistoricCommandGateway.register(any()) }

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
    }

    private companion object {
        val dateTime: LocalDateTime = now()

        @JvmStatic
        fun testScenarios(): Stream<Arguments> {
            return Stream.of(
                pricesMatchedAndSizeMetched(),
                pricesMatchedAndHasMoreToSellThenToBuy(),
                pricesMatchedAndHasLessToSellThenToBuy(),
                sizeMatchedAndPricesDidNot()
            )
        }

        private fun pricesMatchedAndSizeMetched(): Arguments {
            return arguments(
                Wallet(1, TEN, 10),
                Wallet(2, TEN, 10),
                SellOrder(TEN, 10, 1, dateTime, 1, IN_TRADE),
                BuyOrder(TEN, 10, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("110"), 10),
                Wallet(2, TEN, 20),
                SellOrder(TEN, 0, 1, dateTime, 1, CLOSED),
                BuyOrder(TEN, 0, 2, dateTime, 2, CLOSED)
            )
        }

        private fun pricesMatchedAndHasMoreToSellThenToBuy(): Arguments {
            return arguments(
                Wallet(1, TEN, 10),
                Wallet(2, TEN, 10),
                SellOrder(TEN, 15, 1, dateTime, 1, IN_TRADE),
                BuyOrder(TEN, 10, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("110"), 10),
                Wallet(2, TEN, 20),
                SellOrder(TEN, 5, 1, dateTime, 1, IN_TRADE),
                BuyOrder(TEN, 0, 2, dateTime, 2, CLOSED)
            )
        }

        private fun pricesMatchedAndHasLessToSellThenToBuy(): Arguments {
            return arguments(
                Wallet(1, TEN, 10),
                Wallet(2, TEN, 10),
                SellOrder(TEN, 10, 1, dateTime, 1, IN_TRADE),
                BuyOrder(TEN, 15, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("110"), 10),
                Wallet(2, TEN, 20),
                SellOrder(TEN, 0, 1, dateTime, 1, CLOSED),
                BuyOrder(TEN, 5, 2, dateTime, 2, IN_TRADE)
            )
        }

        private fun sizeMatchedAndPricesDidNot(): Arguments {
            return arguments(
                Wallet(1, TEN, 10),
                Wallet(2, TEN, 10),
                SellOrder(BigDecimal("9"), 10, 1, dateTime, 1, IN_TRADE),
                BuyOrder(TEN, 10, 2, dateTime, 2, IN_TRADE),
                Wallet(1, BigDecimal("100"), 10),
                Wallet(2, TEN, 20),
                SellOrder(BigDecimal("9"), 0, 1, dateTime, 1, CLOSED),
                BuyOrder(TEN, 0, 2, dateTime, 2, CLOSED)
            )
        }
    }
}