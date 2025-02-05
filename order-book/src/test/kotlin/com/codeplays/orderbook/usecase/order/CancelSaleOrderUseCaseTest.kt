package com.codeplays.orderbook.usecase.order

import com.codeplays.orderbook.entity.order.exception.InvalidOrderType
import com.codeplays.orderbook.entity.order.gateway.OrderCommandGateway
import com.codeplays.orderbook.entity.order.gateway.OrderQueryGateway
import com.codeplays.orderbook.entity.order.model.Order
import com.codeplays.orderbook.entity.order.model.Order.State.CANCELLED
import com.codeplays.orderbook.entity.order.model.Order.State.TRADING
import com.codeplays.orderbook.entity.order.model.Order.Type.PURCHASE
import com.codeplays.orderbook.entity.order.model.Order.Type.SALE
import com.codeplays.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.codeplays.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.codeplays.orderbook.entity.wallet.model.Wallet
import com.codeplays.orderbook.usecase.order.CancelOrderUseCase.Input
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime.now

class CancelSaleOrderUseCaseTest {

    private val dateTime = now()

    @MockK
    lateinit var orderQueryGateway: OrderQueryGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway

    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway

    @MockK
    lateinit var walletCommandGateway: WalletCommandGateway

    @InjectMockKs
    lateinit var cancelSaleOrderUseCase: CancelSaleOrderUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should cancel a order`() {

        val inTradeOrder = Order(
            id = 1,
            walletId = 1,
            type = SALE,
            price = BigDecimal("10"),
            size = 10,
            creationDate = dateTime,
            state = TRADING
        )

        val orderSlot = slot<Order>()
        val walletSlot = slot<Wallet>()

        every { orderQueryGateway.findById(eq(1)) } returns inTradeOrder
        every { walletQueryGateway.findById(eq(1)) } returns Wallet(id = 1, amountOfMoney = BigDecimal("10"), amountOfVibranium = 10)
        every { orderCommandGateway.update(capture(orderSlot)) } just Runs
        every { walletCommandGateway.update(capture(walletSlot)) } just Runs

        cancelSaleOrderUseCase.execute(Input(1))

        verify(exactly = 1) { orderQueryGateway.findById(1) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CANCELLED, orderSlot.captured.state)
        assertEquals(dateTime, orderSlot.captured.creationDate)
        assertEquals(1, orderSlot.captured.walletId)
        assertEquals(BigDecimal("10"), orderSlot.captured.price)
        assertEquals(SALE, orderSlot.captured.type)
        assertEquals(1, orderSlot.captured.id)

        assertEquals(1, walletSlot.captured.id)
        assertEquals(20, walletSlot.captured.amountOfVibranium)
        assertEquals(BigDecimal("10"), walletSlot.captured.amountOfMoney)
    }

    @Test
    fun `Should not cancel a purchase-order`() {
        val inTradeOrder = Order(
            id = 1,
            walletId = 1,
            type = PURCHASE,
            price = BigDecimal("10"),
            size = 10,
            creationDate = dateTime,
            state = TRADING
        )

        every { orderQueryGateway.findById(eq(1)) } returns inTradeOrder

        val exception = assertThrows<InvalidOrderType> { cancelSaleOrderUseCase.execute(Input(1)) }

        assertEquals("Not a SALE order", exception.message)
    }
}