package com.meli.orderbook.entity.trade.service

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.CLOSED
import com.meli.orderbook.entity.order.model.Order.State.TRADING
import com.meli.orderbook.entity.order.model.Order.Type.PURCHASE
import com.meli.orderbook.entity.order.model.Order.Type.SALE
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
    fun `Should not execute the trade`() {

        tradeService.execute(
            Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
            listOf(Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("15"), size = 10, creationDate = dateTime, state = TRADING))
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
        saleOrder: Order,
        purchaseOrder: Order,
        expectedSellerWallet: Wallet,
        expectedBuyerWallet: Wallet,
        expectedSaleOrder: Order,
        expectedPurchaseOrder: Order,
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

        tradeService.execute(
            saleOrder,
            listOf(purchaseOrder)
        )

        verify(exactly = 1) { walletQueryGateway.findById(eq(sellerWallet.id)) }
        verify(exactly = 1) { walletQueryGateway.findById(eq(buyerWallet.id)) }
        verify(exactly = 2) { walletCommandGateway.update(any()) }
        verify(exactly = 2) { orderCommandGateway.update(any()) }
        verify(exactly = 1) { tradeHistoryCommandGateway.register(any()) }

        val sellerWalletCaptured = walletsSlot.find { it.id == sellerWallet.id }
        assertEquals(expectedSellerWallet.id, sellerWalletCaptured?.id)
        assertEquals(expectedSellerWallet.amountOfVibranium, sellerWalletCaptured?.amountOfVibranium)
        assertEquals(expectedSellerWallet.amountOfMoney, sellerWalletCaptured?.amountOfMoney)

        val buyerWalletCaptured = walletsSlot.find { it.id == buyerWallet.id }
        assertEquals(expectedBuyerWallet.id, buyerWalletCaptured?.id)
        assertEquals(expectedBuyerWallet.amountOfVibranium, buyerWalletCaptured?.amountOfVibranium)
        assertEquals(expectedBuyerWallet.amountOfMoney, buyerWalletCaptured?.amountOfMoney)

        val saleOrderCaptured = ordersSlot.find { it.id == saleOrder.id }
        assertEquals(expectedSaleOrder.id, saleOrderCaptured?.id)
        assertEquals(expectedSaleOrder.walletId, saleOrderCaptured?.walletId)
        assertEquals(expectedSaleOrder.size, saleOrderCaptured?.size)
        assertEquals(expectedSaleOrder.creationDate, saleOrderCaptured?.creationDate)
        assertEquals(expectedSaleOrder.price, saleOrderCaptured?.price)
        assertEquals(expectedSaleOrder.type, saleOrderCaptured?.type)
        assertEquals(expectedSaleOrder.state, saleOrderCaptured?.state)

        val purchaseOrderCaptured = ordersSlot.find { it.id == purchaseOrder.id }
        assertEquals(expectedPurchaseOrder.id, purchaseOrderCaptured?.id)
        assertEquals(expectedPurchaseOrder.walletId, purchaseOrderCaptured?.walletId)
        assertEquals(expectedPurchaseOrder.size, purchaseOrderCaptured?.size)
        assertEquals(expectedPurchaseOrder.creationDate, purchaseOrderCaptured?.creationDate)
        assertEquals(expectedPurchaseOrder.price, purchaseOrderCaptured?.price)
        assertEquals(expectedPurchaseOrder.type, purchaseOrderCaptured?.type)
        assertEquals(expectedPurchaseOrder.state, purchaseOrderCaptured?.state)

        assertNull(tradeSlot.captured.id)
        assertEquals(expectedTrade.size, tradeSlot.captured.size)
        assertEquals(expectedTrade.price, tradeSlot.captured.price)
        assertEquals(expectedTrade.change, tradeSlot.captured.change)
        assertEquals(expectedTrade.saleOrderId, tradeSlot.captured.saleOrderId)
        assertEquals(expectedTrade.purchaseOrderId, tradeSlot.captured.purchaseOrderId)
        assertEquals(expectedTrade.saleWalletId, tradeSlot.captured.saleWalletId)
        assertEquals(expectedTrade.purchaseWalletId, tradeSlot.captured.purchaseWalletId)
        assertEquals(expectedTrade.type, tradeSlot.captured.type)
        assertNotNull(tradeSlot.captured.creationDate)
    }

    private companion object {
        val dateTime: LocalDateTime = now()

        @JvmStatic
        fun testScenarios(): Stream<Arguments> {
            return Stream.of(
                `sale-trade where price matches and size metches`(),
                `sale-trade where price matches and sale-size is greater than purchase-size`(),
                `sale-trade where price matches and sale-size is less than purchase-size`(),
                `sale-trade where sale-price is cheaper than purchase-price and size are equal`(),
                `sale-trade where sale-price is cheaper than purchase-price and sale-size is greater than purchase-size`(),
                `sale-trade where sale-price is cheaper than purchase-price and sale-size is less than purchase-size`(),
                `purchase-trade where price matches and size metches`()
            )
        }

        private fun `sale-trade where price matches and size metches`(): Arguments {
            return arguments(
                Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Wallet(id = 1, amountOfMoney = BigDecimal("110"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 20),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 0, creationDate = dateTime, state =  CLOSED),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 0, creationDate = dateTime, state =  CLOSED),
                Trade(saleOrderId = 1, purchaseOrderId = 2, saleWalletId = 1, purchaseWalletId = 2, type = SALE, size = 10, price = BigDecimal("10"), change = BigDecimal("0"), creationDate = dateTime)
            )
        }

        private fun `sale-trade where price matches and sale-size is greater than purchase-size`(): Arguments {
            return arguments(
                Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 15, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Wallet(id = 1, amountOfMoney = BigDecimal("110"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 20),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 5, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 0, creationDate = dateTime, state = CLOSED),
                Trade(saleOrderId = 1, purchaseOrderId = 2, saleWalletId = 1, purchaseWalletId = 2, type = SALE, size = 10, price = BigDecimal("10"), change = BigDecimal("0"), creationDate = dateTime)
            )
        }

        private fun `sale-trade where price matches and sale-size is less than purchase-size`(): Arguments {
            return arguments(
                Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 15, creationDate = dateTime, state = TRADING),
                Wallet(id = 1, amountOfMoney = BigDecimal("110"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 20),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 0, creationDate = dateTime, state = CLOSED),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 5, creationDate = dateTime, state = TRADING),
                Trade(saleOrderId = 1, purchaseOrderId = 2, saleWalletId = 1, purchaseWalletId = 2, type = SALE, size = 10, price = BigDecimal("10"), change = BigDecimal("0"), creationDate = dateTime)
            )
        }

        private fun `sale-trade where sale-price is cheaper than purchase-price and size are equal`(): Arguments {
            return arguments(
                Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("9"), size = 10, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Wallet(id = 1, amountOfMoney = BigDecimal("100"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("20"), amountOfVibranium = 20),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("9"), size = 0, creationDate = dateTime, state = CLOSED),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 0, creationDate = dateTime, state = CLOSED),
                Trade(saleOrderId = 1, purchaseOrderId = 2, saleWalletId = 1, purchaseWalletId = 2, type = SALE, size = 10, price = BigDecimal("9"), change = BigDecimal("1"), creationDate = dateTime)
            )
        }

        private fun `sale-trade where sale-price is cheaper than purchase-price and sale-size is greater than purchase-size`(): Arguments {
            return arguments(
                Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("9"), size = 15, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Wallet(id = 1, amountOfMoney = BigDecimal("100"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("20"), amountOfVibranium = 20),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("9"), size = 5, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 0, creationDate =  dateTime, state = CLOSED),
                Trade(saleOrderId = 1, purchaseOrderId = 2, saleWalletId = 1, purchaseWalletId = 2, type = SALE, size = 10, price = BigDecimal("9"), change = BigDecimal("1"), creationDate = dateTime)
            )
        }

        private fun `sale-trade where sale-price is cheaper than purchase-price and sale-size is less than purchase-size`(): Arguments {
            return arguments(
                Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("9"), size = 10, creationDate = dateTime, state = TRADING),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 15, creationDate = dateTime, state = TRADING),
                Wallet(id = 1, amountOfMoney = BigDecimal("100"), amountOfVibranium = 10),
                Wallet(id = 2, amountOfMoney = BigDecimal("20"), amountOfVibranium = 20),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("9"), size = 0, creationDate =  dateTime, state = CLOSED),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 5, creationDate = dateTime, state = TRADING),
                Trade(saleOrderId = 1, purchaseOrderId = 2, saleWalletId = 1, purchaseWalletId = 2, type = SALE, size = 10, price = BigDecimal("9"), change = BigDecimal("1"), creationDate = dateTime)
            )
        }

        private fun `purchase-trade where price matches and size metches`(): Arguments {
            return arguments(
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 10, creationDate = dateTime, state = TRADING),
                Wallet(id = 2, amountOfMoney = BigDecimal("10"), amountOfVibranium = 20),
                Wallet(id = 1, amountOfMoney = BigDecimal("110"), amountOfVibranium = 10),
                Order(id = 2, walletId = 2, type = PURCHASE, price = BigDecimal("10"), size = 0, creationDate =  dateTime, state = CLOSED),
                Order(id = 1, walletId = 1, type = SALE, price = BigDecimal("10"), size = 0, creationDate =  dateTime, state = CLOSED),
                Trade(saleOrderId = 1, purchaseOrderId = 2, saleWalletId = 1, purchaseWalletId = 2, type = PURCHASE, size = 10, price = BigDecimal("10"), change = BigDecimal("0"), creationDate = dateTime)
            )
        }

    }
}