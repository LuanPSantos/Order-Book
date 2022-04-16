package com.meli.orderbook.usecase.order

import com.meli.orderbook.entity.order.gateway.OrderCommandGateway
import com.meli.orderbook.entity.order.gateway.OrderQueryGateway
import com.meli.orderbook.entity.order.model.Order
import com.meli.orderbook.entity.order.model.Order.State.CANCELLED
import com.meli.orderbook.entity.order.model.Order.State.IN_TRADE
import com.meli.orderbook.entity.order.model.Order.Type.SELL
import com.meli.orderbook.entity.wallet.gateway.WalletCommandGateway
import com.meli.orderbook.entity.wallet.gateway.WalletQueryGateway
import com.meli.orderbook.entity.wallet.model.Wallet
import com.meli.orderbook.usecase.order.CancelOrderUseCase.Input
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime.now

class CancelSellOrderUseCaseTest {

    @MockK
    lateinit var orderQueryGateway: OrderQueryGateway

    @MockK
    lateinit var orderCommandGateway: OrderCommandGateway
    @MockK
    lateinit var walletQueryGateway: WalletQueryGateway
    @MockK
    lateinit var walletCommandGateway: WalletCommandGateway

    @InjectMockKs
    lateinit var cancelSellOrderUseCase: CancelSellOrderUseCase

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `Should cancel a order`() {
        val dateTime = now()
        val inTradeOrder = Order(1, SELL, BigDecimal("10"), 10, dateTime, IN_TRADE, 1)

        val orderSlot = slot<Order>()
        val walletSlot = slot<Wallet>()

        every { orderQueryGateway.findById(eq(1)) } returns inTradeOrder
        every { walletQueryGateway.findById(eq(1)) } returns Wallet(1, BigDecimal("10"), 10)
        every { orderCommandGateway.update(capture(orderSlot)) } just Runs
        every { walletCommandGateway.update(capture(walletSlot)) } just Runs

        cancelSellOrderUseCase.execute(Input(1))

        verify(exactly = 1) { orderQueryGateway.findById(1) }
        verify(exactly = 1) { orderCommandGateway.update(any()) }

        assertEquals(CANCELLED, orderSlot.captured.state)
        assertEquals(dateTime, orderSlot.captured.creationDate)
        assertEquals(1, orderSlot.captured.walletId)
        assertEquals(BigDecimal("10"), orderSlot.captured.price)
        assertEquals(SELL, orderSlot.captured.type)
        assertEquals(1, orderSlot.captured.id)

        assertEquals(1, walletSlot.captured.id)
        assertEquals(20, walletSlot.captured.amountOfVibranium)
        assertEquals(BigDecimal("10"), walletSlot.captured.amountOfMoney)
    }
}